#!/usr/bin/env bash
# setup-linux.sh — First-time Linux setup for ODROID-M2 refilling station.
#
# Run once on a fresh Ubuntu 22.04 / 24.04 image (Hardkernel official build).
# Must be run as root:  sudo bash setup-linux.sh
#
# What this script does:
#   1. Updates system packages
#   2. Installs Python 3.11, gpiod, Chromium, Openbox, curl
#   3. Creates a 'station' system user with GPIO/dialout group access
#   4. Copies service files into /opt/station/
#   5. Creates a Python virtual environment and installs Python deps
#   6. Installs and enables the three systemd services
#   7. Disables screen blanking / power saving on the display
#
# After running this script:
#   1. Copy your .env file:   sudo cp your.env /opt/station/hardware_service/.env
#   2. Copy your AWS certs:   sudo cp -r certs/ /opt/station/hardware_service/certs/
#   3. Reboot:                sudo reboot

set -euo pipefail

STATION_USER=station
INSTALL_DIR=/opt/station
SERVICE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SERVICE_DIR/../.." && pwd)"
HW_SERVICE_SRC="$REPO_ROOT/hardware/m2_hardware_service"
KIOSK_UI_SRC="$REPO_ROOT/kiosk_ui"

log() { echo "[setup] $*"; }
die() { echo "[setup] ERROR: $*" >&2; exit 1; }

[[ $EUID -eq 0 ]] || die "Run as root: sudo bash setup-linux.sh"

# ── 1. System packages ────────────────────────────────────────────────────────
log "Updating package lists..."
apt-get update -qq

log "Installing system packages..."
apt-get install -y --no-install-recommends \
    python3.11 \
    python3.11-venv \
    python3-pip \
    python3-gpiod \
    libgpiod2 \
    chromium-browser \
    openbox \
    xorg \
    xinput \
    x11-xserver-utils \
    curl \
    git \
    unclutter \
    ca-certificates

# ── 2. Create station user ────────────────────────────────────────────────────
log "Creating '$STATION_USER' system user..."
if ! id "$STATION_USER" &>/dev/null; then
    useradd --system --create-home --shell /bin/bash \
            --groups gpio,dialout,video,input \
            "$STATION_USER"
else
    log "User '$STATION_USER' already exists — adding groups..."
    usermod -aG gpio,dialout,video,input "$STATION_USER" || true
fi

# ── 3. Create install directories ─────────────────────────────────────────────
log "Creating $INSTALL_DIR..."
mkdir -p "$INSTALL_DIR/hardware_service"
mkdir -p "$INSTALL_DIR/kiosk_ui"
mkdir -p "$INSTALL_DIR/hardware_service/certs"

# ── 4. Copy service source files ──────────────────────────────────────────────
log "Copying hardware service source..."
cp -r "$HW_SERVICE_SRC"/. "$INSTALL_DIR/hardware_service/"

log "Copying kiosk UI..."
cp -r "$KIOSK_UI_SRC"/. "$INSTALL_DIR/kiosk_ui/"

chown -R "$STATION_USER:$STATION_USER" "$INSTALL_DIR"

# ── 5. Python virtual environment ─────────────────────────────────────────────
log "Creating Python virtual environment..."
python3.11 -m venv "$INSTALL_DIR/venv"
"$INSTALL_DIR/venv/bin/pip" install --upgrade pip --quiet
"$INSTALL_DIR/venv/bin/pip" install \
    --requirement "$INSTALL_DIR/hardware_service/requirements.txt" \
    --quiet

# ── 6. Configure .env file ────────────────────────────────────────────────────
if [[ ! -f "$INSTALL_DIR/hardware_service/.env" ]]; then
    log "Copying .env.example — EDIT THIS before starting the service!"
    cp "$INSTALL_DIR/hardware_service/.env.example" \
       "$INSTALL_DIR/hardware_service/.env"
    chown "$STATION_USER:$STATION_USER" "$INSTALL_DIR/hardware_service/.env"
    chmod 600 "$INSTALL_DIR/hardware_service/.env"
fi

# ── 7. Chromium preferences (disable restore/crash prompts) ──────────────────
CHROMIUM_PREFS_DIR="/home/$STATION_USER/.config/chromium/Default"
mkdir -p "$CHROMIUM_PREFS_DIR"
cat > "$CHROMIUM_PREFS_DIR/Preferences" <<'EOF'
{
  "profile": { "exit_type": "Normal", "exited_cleanly": true },
  "browser": { "check_default_browser": false }
}
EOF
chown -R "$STATION_USER:$STATION_USER" "/home/$STATION_USER/.config"

# ── 8. Openbox autostart (disable screensaver / DPMS) ────────────────────────
OB_CONFIG_DIR="/home/$STATION_USER/.config/openbox"
mkdir -p "$OB_CONFIG_DIR"
cat > "$OB_CONFIG_DIR/autostart" <<'EOF'
# Disable screen blanking and power saving on the kiosk display
xset s off &
xset s noblank &
xset -dpms &
# Hide mouse cursor after 2 seconds of inactivity
unclutter -idle 2 -root &
EOF
chown -R "$STATION_USER:$STATION_USER" "$OB_CONFIG_DIR"

# ── 9. Install systemd services ───────────────────────────────────────────────
log "Installing systemd service units..."
cp "$SERVICE_DIR/station-xorg.service"     /etc/systemd/system/
cp "$SERVICE_DIR/station-hardware.service" /etc/systemd/system/
cp "$SERVICE_DIR/station-kiosk.service"    /etc/systemd/system/

systemctl daemon-reload
systemctl enable station-xorg
systemctl enable station-hardware
systemctl enable station-kiosk

# ── 10. Set default boot target to graphical ──────────────────────────────────
systemctl set-default graphical.target

log ""
log "=========================================================="
log "  Setup complete!"
log ""
log "  Next steps before rebooting:"
log "  1. Edit .env:"
log "       sudo nano $INSTALL_DIR/hardware_service/.env"
log "  2. Copy AWS IoT certificates into:"
log "       $INSTALL_DIR/hardware_service/certs/"
log "  3. Reboot:"
log "       sudo reboot"
log "=========================================================="
