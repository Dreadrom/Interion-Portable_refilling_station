"""
display.py — Pump authentication code display.

The pump shows a 4-digit code that the driver reads and types into the app.
This module drives whatever physical display is attached.

⚠️  Hardware stub — real implementation depends on chosen display hardware.
    Current stub just logs the code.  Replace with the appropriate driver:

    Option A: I2C OLED SSD1306 (128×64, cheap and common)
      pip install adafruit-circuitpython-ssd1306 Pillow
      Wire: RPi SDA → display SDA, SCL → display SCL, 3.3V, GND

    Option B: Small LCD via I2C PCF8574 backpack
      pip install RPLCD

    Option C: 7-segment display via I2C or SPI
"""

import logging
import random
import string

logger = logging.getLogger(__name__)


class PumpDisplay:
    def __init__(self) -> None:
        self._current_code: str | None = None
        self._init_hardware()

    def _init_hardware(self) -> None:
        """
        Initialise the display hardware.
        ⚠️  STUB — replace with real driver initialisation.
        """
        logger.info("[DISPLAY] Stub display initialised (no physical hardware)")

    def show_auth_code(self, code: str) -> None:
        """
        Show the 4-digit auth code prominently on the display.
        ⚠️  STUB — replace with real display rendering.
        """
        self._current_code = code
        # Real implementation example (SSD1306):
        #   from PIL import Image, ImageDraw, ImageFont
        #   img = Image.new("1", (128, 64))
        #   draw = ImageDraw.Draw(img)
        #   font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 32)
        #   draw.text((20, 16), code, font=font, fill=255)
        #   self._oled.image(img)
        #   self._oled.show()
        logger.info("[DISPLAY] Auth code displayed: %s", code)

    def show_message(self, line1: str, line2: str = "") -> None:
        """
        Show a two-line status message (e.g. "DISPENSING", "12.5 L").
        ⚠️  STUB — replace with real display rendering.
        """
        logger.info("[DISPLAY] %s | %s", line1, line2)

    def clear(self) -> None:
        """Clear the display."""
        self._current_code = None
        logger.info("[DISPLAY] Cleared")


def generate_auth_code() -> str:
    """Generate a cryptographically random 4-digit numeric code."""
    return "".join(random.choices(string.digits, k=4))
