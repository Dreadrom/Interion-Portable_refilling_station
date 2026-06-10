import { router } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useState, useEffect, useCallback } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Modal,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import * as MailComposer from 'expo-mail-composer';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';
import { useAuthStore } from '../../src/stores/useAuthStore';
import { buildReceiptNumber, stationSeqFor } from '../../src/utils/receiptNumber';

const SST_RATE = 0.08;
const COMPANY_SST_REG = 'W10-1910-32000001';

function formatDate(ts: number): string {
  return new Date(ts).toLocaleDateString('en-MY', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

function formatDateShort(ts: number): string {
  const d = new Date(ts);
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}`;
}

function formatTime(ts: number): string {
  return new Date(ts).toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit' });
}

function stopReasonInfo(r: string): { label: string; color: string } {
  switch (r) {
    case 'TARGET_REACHED': return { label: 'Order Fulfilled', color: '#10B981' };
    case 'TANK_FULL':      return { label: 'Tank full',       color: '#6366F1' };
    case 'USER_STOPPED':   return { label: 'Stopped by user', color: '#F59E0B' };
    case 'EMERGENCY_STOP': return { label: 'Emergency stop',  color: '#EF4444' };
    case 'TIMEOUT':        return { label: 'Timed out',       color: '#6B7280' };
    default:               return { label: r,                 color: '#6B7280' };
  }
}

function buildReceiptHTML(txn: TransactionData, seq: number, userName?: string, userEmail?: string): string {
  const total = txn.amountCharged;
  const sst   = total * SST_RATE / (1 + SST_RATE);
  const subtotal = total - sst;
  const receiptNo = buildReceiptNumber(txn.stationName, seq);
  const dateStr = formatDateShort(txn.timestamp);
  const startTimeStr = new Date(txn.dispensingStartTime).toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const endTimeStr   = new Date(txn.timestamp).toLocaleTimeString('en-MY',           { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const billToBlock = userName
    ? `<div class="bill-box">
         <div class="bill-title">Bill To</div>
         <div class="bill-row"><strong>${userName}</strong></div>
         ${userEmail ? `<div class="bill-row">${userEmail}</div>` : ''}
       </div>`
    : '';

  return `<!DOCTYPE html>
<html><head><meta charset="utf-8"><style>
*{box-sizing:border-box;margin:0;padding:0}
@page{margin:18mm 14mm}
body{font-family:"Times New Roman",Times,serif;font-size:11pt;color:#000;background:#fff}
.page{max-width:700px;margin:0 auto;padding:24px}
.lh{text-align:center;padding-bottom:14px;border-bottom:3px double #000;margin-bottom:18px}
.co-name{font-size:17pt;font-weight:bold;letter-spacing:1px;text-transform:uppercase}
.co-tag{font-size:9pt;margin-top:4px}
.co-info{font-size:9pt;margin-top:6px;line-height:1.7}
.doc-title{text-align:center;font-size:13pt;font-weight:bold;text-transform:uppercase;letter-spacing:2px;border:2px solid #000;padding:6px 0;margin-bottom:18px}
.meta{width:100%;border-collapse:collapse;margin-bottom:14px;font-size:10pt}
.meta td{padding:4px 6px;vertical-align:top}
.ml{font-weight:bold;width:28%;white-space:nowrap}
.mc{width:10px;color:#444}
.bill-box{border:1px solid #000;padding:10px 14px;margin-bottom:16px}
.bill-title{font-weight:bold;font-size:9pt;text-transform:uppercase;letter-spacing:.5px;padding-bottom:5px;margin-bottom:7px;border-bottom:1px solid #ccc}
.bill-row{font-size:10pt;margin-bottom:3px}
.items{width:100%;border-collapse:collapse}
.items th{font-size:10pt;font-weight:bold;padding:7px 8px;border:1px solid #000;text-align:left;background:#efefef}
.items td{font-size:10pt;padding:7px 8px;border:1px solid #000;vertical-align:top}
.r{text-align:right}.c{text-align:center}
.totals{width:55%;margin-left:auto;border-collapse:collapse;margin-bottom:16px}
.totals td{font-size:10pt;padding:5px 10px;border:1px solid #bbb}
.tr-total td{border:2px solid #000!important;font-weight:bold;font-size:12pt;padding:7px 10px}
.pay-box{border:1px solid #000;padding:9px 14px;margin-bottom:16px;font-size:10pt}
.pay-title{font-weight:bold;margin-bottom:5px}
.pay-row{display:flex;justify-content:space-between;margin-bottom:3px}
.note{font-size:8pt;font-style:italic;color:#444;margin-bottom:20px}
.sig{display:flex;justify-content:space-between;margin-top:32px}
.sig-bl{width:44%;text-align:center}
.sig-line{border-top:1px solid #000;padding-top:5px;font-size:9pt;margin-bottom:4px}
.footer{text-align:center;font-size:8pt;color:#444;border-top:1px dashed #999;padding-top:9px;margin-top:18px;line-height:1.8}
</style></head><body>
<div class="page">
<div class="lh">
  <div class="co-name">BlueDiesel (M) Sdn Bhd</div>
  <div class="co-tag">Portable Refilling Station Network</div>
  <div class="co-info">info@bluediesel.com.my &nbsp;|&nbsp; bluediesel.com.my<br>SST Reg. No.: ${COMPANY_SST_REG}</div>
</div>
<div class="doc-title">Official Tax Receipt</div>
<table class="meta">
  <tr>
    <td class="ml">Receipt No.</td><td class="mc">:</td><td><strong>${receiptNo}</strong></td>
    <td class="ml">Date</td><td class="mc">:</td><td>${dateStr}</td>
  </tr><tr>
    <td class="ml">Station</td><td class="mc">:</td><td>${txn.stationName}</td>
    <td class="ml">Pump Start</td><td class="mc">:</td><td>${startTimeStr}</td>
  </tr><tr>
    <td class="ml">Station ID</td><td class="mc">:</td><td>${txn.stationId}</td>
    <td class="ml">Pump End</td><td class="mc">:</td><td>${endTimeStr}</td>
  </tr><tr>
    <td class="ml">Address</td><td class="mc">:</td><td colspan="4">${txn.stationAddress || '&mdash;'}</td>
  </tr><tr>
    <td class="ml">Phone</td><td class="mc">:</td><td>${txn.stationPhone || '&mdash;'}</td>
    <td class="ml">Terminal ID</td><td class="mc">:</td><td>${txn.terminalId}</td>
  </tr><tr>
    <td class="ml">Nozzle / Pump</td><td class="mc">:</td><td>#${txn.nozzle}</td>
    <td class="ml">Payment Method</td><td class="mc">:</td><td>BlueDiesel Digital Wallet</td>
  </tr>
</table>
${billToBlock}
<table class="items">
  <tr>
    <th class="c" style="width:28px">No.</th>
    <th>Description</th>
    <th class="c" style="width:85px">Volume (L)</th>
    <th class="r" style="width:115px">Unit Price (${txn.currency}/L)</th>
    <th class="r" style="width:100px">Amount (${txn.currency})</th>
  </tr>
  <tr>
    <td class="c">1</td>
    <td>${txn.product}<br><span style="font-size:9pt;color:#555">Nozzle #${txn.nozzle} &nbsp;|&nbsp; Duration: ${txn.elapsedTime} &nbsp;|&nbsp; ${startTimeStr} &ndash; ${endTimeStr}</span></td>
    <td class="c">${txn.volumeDispensed.toFixed(3)}</td>
    <td class="r">${txn.unitPrice.toFixed(4)}</td>
    <td class="r">${subtotal.toFixed(2)}</td>
  </tr>
</table>
<table class="totals">
  <tr><td>Subtotal (excl. SST)</td><td class="r">${txn.currency} ${subtotal.toFixed(2)}</td></tr>
  <tr><td>SST @ 8%</td><td class="r">${txn.currency} ${sst.toFixed(2)}</td></tr>
  <tr class="tr-total"><td>TOTAL PAYABLE</td><td class="r">${txn.currency} ${total.toFixed(2)}</td></tr>
</table>
<div class="pay-box">
  <div class="pay-title">Payment Details</div>
  <div class="pay-row"><span>Payment Method</span><span>BlueDiesel Digital Wallet</span></div>
  <div class="pay-row"><span>Amount Paid</span><span>${txn.currency} ${total.toFixed(2)}</span></div>
  <div class="pay-row"><span>Approval Code</span><span>${txn.approvalCode}</span></div>
  <div class="pay-row"><span>Reference No.</span><span>${txn.paymentRef}</span></div>
  <div class="pay-row" style="margin-top:4px;border-top:1px solid #ccc;padding-top:4px"><span>Status</span><span><strong>APPROVED</strong></span></div>
</div>
<p class="note">* This is a computer-generated receipt. No signature is required. E. &amp; O.E.</p>
<div class="sig">
  <div class="sig-bl"><div class="sig-line">&nbsp;</div><div>Customer Signature</div></div>
  <div class="sig-bl"><div class="sig-line">&nbsp;</div><div>Authorised Signature &amp; Stamp<br>For BlueDiesel (M) Sdn Bhd</div></div>
</div>
<div class="footer">
  Thank you for your business. Please retain this receipt for your records.<br>
  For inquiries: info@bluediesel.com.my &nbsp;|&nbsp; bluediesel.com.my
</div>
</div></body></html>`;
}

// ─── Receipt detail modal ────────────────────────────────────────────────────
function ReceiptModal({
  txn,
  seq,
  visible,
  onClose,
}: {
  txn: TransactionData;
  seq: number;
  visible: boolean;
  onClose: () => void;
}) {
  const insets = useSafeAreaInsets();
  const { user, isGuest } = useAuthStore();
  const [busy, setBusy] = useState(false);

  const total = txn.amountCharged;
  const sst   = total * SST_RATE / (1 + SST_RATE);
  const receiptNo    = buildReceiptNumber(txn.stationName, seq);
  const dateStr      = formatDateShort(txn.timestamp);
  const startTimeStr = new Date(txn.dispensingStartTime).toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const endTimeStr   = new Date(txn.timestamp).toLocaleTimeString('en-MY',           { hour: '2-digit', minute: '2-digit', second: '2-digit' });

  const getHtml = () => buildReceiptHTML(
    txn,
    seq,
    (!isGuest && user?.name) ? user.name : undefined,
    (!isGuest && user?.email) ? user.email : undefined,
  );

  const getPdfUri = async () => {
    const { uri } = await Print.printToFileAsync({ html: getHtml(), base64: false });
    return uri;
  };

  const handleViewPdf = async () => {
    setBusy(true);
    try {
      const uri = await getPdfUri();
      await Sharing.shareAsync(uri, { mimeType: 'application/pdf', dialogTitle: 'View / Save Invoice' });
    } catch (e: any) {
      Alert.alert('Error', e.message || 'Could not generate PDF.');
    } finally {
      setBusy(false);
    }
  };

  const handleEmail = async () => {
    setBusy(true);
    try {
      const available = await MailComposer.isAvailableAsync();
      if (!available) {
        Alert.alert('Email unavailable', 'No mail app is configured on this device.');
        setBusy(false);
        return;
      }
      const uri = await getPdfUri();
      await MailComposer.composeAsync({
        subject: `BlueDiesel Receipt - ${receiptNo}`,
        body: `Receipt No: ${receiptNo}\nStation: ${txn.stationName}\nTotal: ${txn.currency} ${total.toFixed(2)}\n\nThank you for using BlueDiesel.`,
        attachments: [uri],
        recipients: (!isGuest && user?.email) ? [user.email] : [],
      });
    } catch (e: any) {
      Alert.alert('Error', e.message || 'Could not open mail.');
    } finally {
      setBusy(false);
    }
  };

  const stopInfo = stopReasonInfo(txn.stopReason);
  const timeStr  = formatTime(txn.timestamp);

  return (
    <Modal
      visible={visible}
      animationType="slide"
      presentationStyle="pageSheet"
      onRequestClose={onClose}
    >
      <View style={[mStyles.root, { paddingTop: insets.top }]}>
        {/* Nav */}
        <View style={mStyles.navbar}>
          <TouchableOpacity style={mStyles.navBtn} onPress={onClose}>
            <Ionicons name="close" size={22} color="#000" />
          </TouchableOpacity>
          <Text style={mStyles.navTitle}>Official Tax Receipt</Text>
          <TouchableOpacity style={mStyles.navBtn} onPress={handleViewPdf} disabled={busy}>
            <Ionicons name="share-outline" size={22} color="#000" />
          </TouchableOpacity>
        </View>

        <ScrollView bounces={false} showsVerticalScrollIndicator={false}>
          <View style={mStyles.doc}>
            {/* Letterhead */}
            <View style={mStyles.letterhead}>
              <Text style={mStyles.coName}>BLUEDIESEL (M) SDN BHD</Text>
              <Text style={mStyles.coTag}>Portable Refilling Station Network</Text>
              <Text style={mStyles.coInfo}>info@bluediesel.com.my  |  bluediesel.com.my</Text>
              <Text style={mStyles.coInfo}>SST Reg. No.: {COMPANY_SST_REG}</Text>
            </View>

            {/* Title */}
            <View style={mStyles.docTitleBox}>
              <Text style={mStyles.docTitleText}>OFFICIAL TAX RECEIPT</Text>
            </View>

            {/* Meta */}
            <View style={mStyles.metaBlock}>
              {([
                ['Receipt No.', receiptNo,           'Date',        dateStr],
                ['Station',     txn.stationName,     'Pump Start',  startTimeStr],
                ['Station ID',  txn.stationId,       'Pump End',    endTimeStr],
                ['Address',     txn.stationAddress || '\u2014', 'Terminal', txn.terminalId],
                ['Phone',       txn.stationPhone || '\u2014', 'Nozzle / Pump', `#${txn.nozzle}`],
                ['Status',      stopInfo.label,      '',            ''],
              ] as [string, string, string, string][]).map(([l1, v1, l2, v2], i) => (
                <View key={i} style={mStyles.metaRow}>
                  <Text style={mStyles.metaLabel}>{l1}</Text>
                  <Text style={mStyles.metaColon}>:</Text>
                  <Text style={[mStyles.metaValue, i === 0 && { fontWeight: '700' }]}>{v1}</Text>
                  {l2 ? (
                    <>
                      <Text style={mStyles.metaLabel}>{l2}</Text>
                      <Text style={mStyles.metaColon}>:</Text>
                      <Text style={mStyles.metaValue}>{v2}</Text>
                    </>
                  ) : <Text style={{ flex: 2 }} />}
                </View>
              ))}
            </View>

            {/* Bill To */}
            {!isGuest && user?.name && (
              <View style={mStyles.billBox}>
                <Text style={mStyles.billTitle}>BILL TO</Text>
                <Text style={mStyles.billRow}>{user.name}</Text>
                {user.email ? <Text style={mStyles.billRow}>{user.email}</Text> : null}
              </View>
            )}

            {/* Items table */}
            <View style={mStyles.tableWrap}>
              <View style={mStyles.tableHeader}>
                <Text style={[mStyles.th, { flex: 3 }]}>Description</Text>
                <Text style={[mStyles.th, mStyles.tCenter, { flex: 1 }]}>Vol. (L)</Text>
                <Text style={[mStyles.th, mStyles.tRight, { flex: 1.2 }]}>Unit Price</Text>
                <Text style={[mStyles.th, mStyles.tRight, { flex: 1.2 }]}>Amt ({txn.currency})</Text>
              </View>
              <View style={mStyles.tableRow}>
                <View style={{ flex: 3, borderRightWidth: 1, borderRightColor: '#000' }}>
                  <Text style={mStyles.td}>{txn.product}</Text>
                  <Text style={mStyles.tdSub}>{startTimeStr} – {endTimeStr}  |  Duration: {txn.elapsedTime}</Text>
                </View>
                <Text style={[mStyles.td, mStyles.tCenter, { flex: 1 }]}>{txn.volumeDispensed.toFixed(3)}</Text>
                <Text style={[mStyles.td, mStyles.tRight, { flex: 1.2 }]}>{txn.unitPrice.toFixed(4)}</Text>
                <Text style={[mStyles.td, mStyles.tRight, { flex: 1.2 }]}>{(total - sst).toFixed(2)}</Text>
              </View>
            </View>

            {/* Totals */}
            <View style={mStyles.totalsWrap}>
              <View style={mStyles.totalsRow}>
                <Text style={mStyles.totalsLabel}>Subtotal (excl. SST)</Text>
                <Text style={mStyles.totalsVal}>{txn.currency} {(total - sst).toFixed(2)}</Text>
              </View>
              <View style={mStyles.totalsRow}>
                <Text style={mStyles.totalsLabel}>SST @ 8%</Text>
                <Text style={mStyles.totalsVal}>{txn.currency} {sst.toFixed(2)}</Text>
              </View>
              <View style={mStyles.totalsFinalRow}>
                <Text style={mStyles.totalsFinalLabel}>TOTAL PAYABLE</Text>
                <Text style={mStyles.totalsFinalVal}>{txn.currency} {total.toFixed(2)}</Text>
              </View>
            </View>

            {/* Payment */}
            <View style={mStyles.payBox}>
              <Text style={mStyles.payBoxTitle}>Payment Details</Text>
              <View style={mStyles.payRow}><Text style={mStyles.payBoxLabel}>Method</Text><Text style={mStyles.payBoxVal}>BlueDiesel Digital Wallet</Text></View>
              <View style={mStyles.payRow}><Text style={mStyles.payBoxLabel}>Amount Paid</Text><Text style={mStyles.payBoxVal}>{txn.currency} {total.toFixed(2)}</Text></View>
              <View style={mStyles.payRow}><Text style={mStyles.payBoxLabel}>Approval Code</Text><Text style={mStyles.payBoxVal}>{txn.approvalCode}</Text></View>
              <View style={mStyles.payRow}><Text style={mStyles.payBoxLabel}>Reference No.</Text><Text style={mStyles.payBoxVal}>{txn.paymentRef}</Text></View>
              <View style={[mStyles.payRow, mStyles.payRowFinal]}><Text style={mStyles.payBoxLabel}>Status</Text><Text style={[mStyles.payBoxVal, { fontWeight: '700' }]}>APPROVED</Text></View>
            </View>

            <Text style={mStyles.eoe}>* Computer-generated receipt. No signature required. E. &amp; O.E.</Text>
          </View>

          {/* Action rows */}
          <View style={mStyles.actions}>
            <TouchableOpacity style={mStyles.actionRow} onPress={handleViewPdf} disabled={busy}>
              {busy ? (
                <ActivityIndicator size="small" color="#000" />
              ) : (
                <Ionicons name="document-text-outline" size={18} color="#000" />
              )}
              <Text style={mStyles.actionRowText}>View Invoice (PDF)</Text>
              <Ionicons name="chevron-forward" size={18} color="#555" />
            </TouchableOpacity>

            <TouchableOpacity style={mStyles.actionRow} onPress={handleEmail} disabled={busy}>
              <Ionicons name="mail-outline" size={18} color="#000" />
              <Text style={mStyles.actionRowText}>Email Receipt</Text>
              <Ionicons name="chevron-forward" size={18} color="#555" />
            </TouchableOpacity>

            <TouchableOpacity style={mStyles.actionRow} onPress={handleViewPdf} disabled={busy}>
              <Ionicons name="download-outline" size={18} color="#000" />
              <Text style={mStyles.actionRowText}>Download PDF</Text>
              <Ionicons name="chevron-forward" size={18} color="#555" />
            </TouchableOpacity>
          </View>

          <View style={{ height: insets.bottom + 32 }} />
        </ScrollView>
      </View>
    </Modal>
  );
}

// ─── Transaction list item ───────────────────────────────────────────────────
function TransactionItem({
  item,
  onPress,
}: {
  item: TransactionData;
  onPress: () => void;
}) {
  const stopInfo = stopReasonInfo(item.stopReason);

  return (
    <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.75}>
      <View style={styles.cardHeader}>
        <Text style={styles.stationName} numberOfLines={1}>{item.stationName}</Text>
        <View style={[styles.reasonBadge, { backgroundColor: stopInfo.color + '20' }]}>
          <Text style={[styles.reasonText, { color: stopInfo.color }]}>{stopInfo.label}</Text>
        </View>
      </View>

      <View style={styles.cardBody}>
        <Text style={styles.detail}>{item.product} · Nozzle {item.nozzle}</Text>
        <Text style={styles.detail}>
          {item.volumeDispensed.toFixed(2)} L @ {item.currency} {item.unitPrice.toFixed(2)}/L · {item.elapsedTime}
        </Text>
      </View>

      <View style={styles.cardFooter}>
        <View>
          <Text style={styles.amount}>{item.currency} {item.amountCharged.toFixed(2)}</Text>
          <Text style={styles.timestamp}>{formatDate(item.timestamp)}</Text>
          <Text style={styles.txnId}>#{item.id.slice(-8).toUpperCase()}</Text>
        </View>
        <View style={styles.viewReceiptHint}>
          <Ionicons name="receipt-outline" size={14} color="#10B981" />
          <Text style={styles.viewReceiptText}>Receipt</Text>
          <Ionicons name="chevron-forward" size={13} color="#10B981" />
        </View>
      </View>
    </TouchableOpacity>
  );
}

// ─── Main screen ─────────────────────────────────────────────────────────────
export default function TransactionHistoryScreen() {
  const insets = useSafeAreaInsets();
  const [transactions, setTransactions] = useState<TransactionData[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [selected, setSelected] = useState<TransactionData | null>(null);
  const [selectedSeq, setSelectedSeq] = useState(1);

  const loadTransactions = useCallback(async () => {
    const list = await transactionStore.getAllTransactions();
    setTransactions(list);
    setLoading(false);
    setRefreshing(false);
  }, []);

  useEffect(() => { loadTransactions(); }, [loadTransactions]);

  const totalVolume = transactions.reduce((s, t) => s + t.volumeDispensed, 0);
  const totalSpent  = transactions.reduce((s, t) => s + t.amountCharged, 0);
  const currency    = transactions[0]?.currency || 'MYR';

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Loading transactions...</Text>
      </View>
    );
  }

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backButton}>
          <Ionicons name="chevron-back" size={22} color="#111827" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Transaction History</Text>
        <View style={{ width: 44 }} />
      </View>

      {transactions.length > 0 && (
        <View style={styles.summaryBar}>
          <View style={styles.summaryItem}>
            <Text style={styles.summaryValue}>{transactions.length}</Text>
            <Text style={styles.summaryLabel}>Sessions</Text>
          </View>
          <View style={styles.summaryDivider} />
          <View style={styles.summaryItem}>
            <Text style={styles.summaryValue}>{totalVolume.toFixed(1)} L</Text>
            <Text style={styles.summaryLabel}>Total Volume</Text>
          </View>
          <View style={styles.summaryDivider} />
          <View style={styles.summaryItem}>
            <Text style={styles.summaryValue}>{currency} {totalSpent.toFixed(2)}</Text>
            <Text style={styles.summaryLabel}>Total Spent</Text>
          </View>
        </View>
      )}

      {transactions.length === 0 ? (
        <View style={styles.center}>
          <Ionicons name="reader-outline" size={48} color="#9CA3AF" style={{ marginBottom: 12 }} />
          <Text style={styles.emptyTitle}>No Transactions Yet</Text>
          <Text style={styles.emptySubtitle}>Your refueling transactions will appear here.</Text>
          <TouchableOpacity style={styles.homeButton} onPress={() => router.push('/home')}>
            <Text style={styles.homeButtonText}>Go to Home</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <FlatList
          data={transactions}
          keyExtractor={(item) => item.id}
          renderItem={({ item }) => (
            <TransactionItem
                item={item}
                onPress={() => {
                  setSelectedSeq(stationSeqFor(item.id, item.stationName, transactions));
                  setSelected(item);
                }}
              />
          )}
          contentContainerStyle={[styles.listContent, { paddingBottom: insets.bottom + 24 }]}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); loadTransactions(); }} tintColor="#10B981" />}
        />
      )}

      {selected && (
        <ReceiptModal
          txn={selected}
          seq={selectedSeq}
          visible={!!selected}
          onClose={() => setSelected(null)}
        />
      )}
    </View>
  );
}

// ─── Styles ──────────────────────────────────────────────────────────────────
const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 14,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  backButton: { marginRight: 8, padding: 4 },
  headerTitle: { flex: 1, fontSize: 20, fontWeight: '700', color: '#111827' },

  summaryBar: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    paddingHorizontal: 20,
    paddingVertical: 14,
  },
  summaryItem: { flex: 1, alignItems: 'center' },
  summaryDivider: { width: 1, backgroundColor: '#E5E7EB' },
  summaryValue: { fontSize: 14, fontWeight: '700', color: '#111827', marginBottom: 2 },
  summaryLabel: { fontSize: 11, color: '#6B7280' },

  listContent: { padding: 16 },

  card: {
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.07,
    shadowRadius: 4,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  stationName: { fontSize: 15, fontWeight: '600', color: '#111827', flex: 1, marginRight: 10 },
  reasonBadge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 20 },
  reasonText: { fontSize: 11, fontWeight: '600' },
  cardBody: { marginBottom: 12 },
  detail: { fontSize: 13, color: '#6B7280', marginBottom: 3 },

  cardFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
  },
  amount: { fontSize: 20, fontWeight: '700', color: '#111827', marginBottom: 2 },
  timestamp: { fontSize: 12, color: '#9CA3AF' },
  txnId: { fontSize: 10, color: '#D1D5DB', marginTop: 1 },
  viewReceiptHint: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
    backgroundColor: '#ECFDF5',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#A7F3D0',
  },
  viewReceiptText: { fontSize: 12, fontWeight: '600', color: '#10B981' },

  center: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 32 },
  loadingText: { fontSize: 16, color: '#6B7280', marginTop: 12 },
  emptyTitle: { fontSize: 20, fontWeight: '700', color: '#111827', marginBottom: 8 },
  emptySubtitle: { fontSize: 14, color: '#6B7280', textAlign: 'center', marginBottom: 24 },
  homeButton: {
    backgroundColor: '#10B981',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  homeButtonText: { color: '#fff', fontWeight: '600', fontSize: 15 },
});

// ─── Modal styles ─────────────────────────────────────────────────────────────
const mStyles = StyleSheet.create({
  root: { flex: 1, backgroundColor: '#ECECEC' },

  navbar: {
    flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between',
    paddingHorizontal: 16, paddingVertical: 12,
    backgroundColor: '#fff', borderBottomWidth: 1, borderBottomColor: '#ccc',
  },
  navBtn: { width: 36, height: 36, justifyContent: 'center', alignItems: 'center' },
  navTitle: { fontSize: 15, fontWeight: '700', color: '#000', letterSpacing: 0.3 },

  doc: { margin: 16, backgroundColor: '#fff', borderWidth: 1, borderColor: '#999', padding: 20 },

  letterhead: { alignItems: 'center', paddingBottom: 14, borderBottomWidth: 3, borderBottomColor: '#000', marginBottom: 16 },
  coName: { fontSize: 14, fontWeight: '900', letterSpacing: 0.8, textTransform: 'uppercase', color: '#000', textAlign: 'center' },
  coTag: { fontSize: 11, color: '#444', marginTop: 3, textAlign: 'center' },
  coInfo: { fontSize: 10, color: '#444', marginTop: 3, textAlign: 'center', lineHeight: 15 },

  docTitleBox: { borderWidth: 2, borderColor: '#000', paddingVertical: 7, alignItems: 'center', marginBottom: 16 },
  docTitleText: { fontSize: 12, fontWeight: '900', textTransform: 'uppercase', letterSpacing: 2, color: '#000' },

  metaBlock: { marginBottom: 14 },
  metaRow: { flexDirection: 'row', alignItems: 'flex-start', marginBottom: 4 },
  metaLabel: { width: '28%', fontSize: 11, fontWeight: '700', color: '#000' },
  metaColon: { width: 12, fontSize: 11, color: '#444' },
  metaValue: { flex: 1, fontSize: 11, color: '#000' },

  billBox: { borderWidth: 1, borderColor: '#000', padding: 10, marginBottom: 14 },
  billTitle: { fontSize: 9, fontWeight: '700', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 5, paddingBottom: 4, borderBottomWidth: 1, borderBottomColor: '#ccc' },
  billRow: { fontSize: 11, color: '#000', marginBottom: 2 },

  tableWrap: { borderWidth: 1, borderColor: '#000', marginBottom: 0 },
  tableHeader: { flexDirection: 'row', backgroundColor: '#EFEFEF', borderBottomWidth: 1, borderBottomColor: '#000' },
  tableRow: { flexDirection: 'row' },
  th: { fontSize: 10, fontWeight: '700', color: '#000', padding: 6, borderRightWidth: 1, borderRightColor: '#000' },
  td: { fontSize: 10, color: '#000', padding: 6, borderRightWidth: 1, borderRightColor: '#000' },
  tdSub: { fontSize: 9, color: '#555', paddingHorizontal: 6, paddingBottom: 5 },
  tRight: { textAlign: 'right' },
  tCenter: { textAlign: 'center' },

  totalsWrap: { width: '55%', alignSelf: 'flex-end', borderWidth: 1, borderColor: '#bbb', marginBottom: 14, marginTop: -1 },
  totalsRow: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 4, paddingHorizontal: 8, borderBottomWidth: 1, borderBottomColor: '#ddd' },
  totalsLabel: { fontSize: 10, color: '#000' },
  totalsVal: { fontSize: 10, color: '#000' },
  totalsFinalRow: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 6, paddingHorizontal: 8, borderTopWidth: 2, borderTopColor: '#000' },
  totalsFinalLabel: { fontSize: 11, fontWeight: '700', color: '#000' },
  totalsFinalVal: { fontSize: 11, fontWeight: '700', color: '#000' },

  payBox: { borderWidth: 1, borderColor: '#000', padding: 10, marginBottom: 10 },
  payBoxTitle:  { fontSize: 10, fontWeight: '700', color: '#000', marginBottom: 4 },
  payRow:       { flexDirection: 'row' as const, justifyContent: 'space-between' as const, marginBottom: 3 },
  payRowFinal:  { marginTop: 5, paddingTop: 5, borderTopWidth: 1, borderTopColor: '#ccc' },
  payBoxLabel:  { fontSize: 10, color: '#444' },
  payBoxVal:    { fontSize: 10, color: '#000' },

  eoe: { fontSize: 9, fontStyle: 'italic', color: '#555', marginTop: 6 },

  actions: { paddingHorizontal: 16, backgroundColor: '#fff', borderTopWidth: 1, borderTopColor: '#ddd', marginTop: 8 },
  actionRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 15, borderBottomWidth: 1, borderBottomColor: '#eee', gap: 12 },
  actionRowText: { flex: 1, fontSize: 14, fontWeight: '500', color: '#000' },
});
