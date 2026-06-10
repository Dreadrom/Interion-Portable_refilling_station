import { router, useLocalSearchParams } from 'expo-router';
import React, { useState, useEffect } from 'react';
import {
  ActivityIndicator,
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import * as MailComposer from 'expo-mail-composer';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';
import { buildReceiptNumber, stationSeqFor } from '../../src/utils/receiptNumber';
import { useAuthStore } from '../../src/stores/useAuthStore';

const SST_RATE = 0.08;
const COMPANY_SST_REG = 'W10-1910-32000001';
const COMPANY_NAME = 'BlueDiesel (M) Sdn Bhd';

export default function RefuelingCompleteScreen() {
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams();
  const { isGuest, user } = useAuthStore();
  const [transaction, setTransaction] = useState<TransactionData | null>(null);
  const [loading, setLoading] = useState(true);
  const [receiptSeq, setReceiptSeq] = useState(1);

  const transactionId = params.transactionId as string;
  const currencyParam = (params.currency as string) || 'MYR';

  useEffect(() => { loadTransaction(); }, []);

  const loadTransaction = async () => {
    if (!transactionId) {
      Alert.alert('Error', 'Invalid transaction ID', [{ text: 'OK', onPress: () => router.push('./home') }]);
      return;
    }
    const txn = await transactionStore.getTransaction(transactionId);
    if (!txn) {
      Alert.alert('Error', 'Transaction not found or has expired', [{ text: 'OK', onPress: () => router.push('./home') }]);
      return;
    }
    const allTxns = await transactionStore.getAllTransactions();
    setReceiptSeq(stationSeqFor(transactionId, txn.stationName, allTxns));
    setTransaction(txn);
    setLoading(false);
  };

  if (loading || !transaction) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff' }}>
        <ActivityIndicator size="large" color="#000" />
        <Text style={{ marginTop: 16, color: '#555' }}>Loading receipt...</Text>
      </View>
    );
  }

  const { stationName, stationId, stationAddress, stationPhone, product, nozzle, volumeDispensed,
          amountCharged, unitPrice, currency, elapsedTime, terminalId, approvalCode, paymentRef,
          dispensingStartTime } = transaction;

  const total = amountCharged;
  const sstAmount = total * SST_RATE / (1 + SST_RATE);
  const subtotal = total - sstAmount;

  // Dispensing timestamps — use stored start time and receipt-issued (now) as end time
  const startDt  = new Date(dispensingStartTime);
  const endDt    = new Date(transaction.timestamp);
  const dateStr  = `${endDt.getFullYear()}/${String(endDt.getMonth() + 1).padStart(2, '0')}/${String(endDt.getDate()).padStart(2, '0')}`;
  const startTimeStr = startDt.toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const endTimeStr   = endDt.toLocaleTimeString('en-MY',   { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const receiptNo = buildReceiptNumber(stationName, receiptSeq);

  const generateReceiptHTML = (): string => {
    const billToBlock = (!isGuest && user?.name)
      ? `<div class="bill-box">
           <div class="bill-title">Bill To</div>
           <div class="bill-row"><strong>${user.name}</strong></div>
           ${user.email ? `<div class="bill-row">${user.email}</div>` : ''}
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
    <td class="ml">Station</td><td class="mc">:</td><td>${stationName}</td>
    <td class="ml">Pump Start</td><td class="mc">:</td><td>${startTimeStr}</td>
  </tr><tr>
    <td class="ml">Station ID</td><td class="mc">:</td><td>${stationId}</td>
    <td class="ml">Pump End</td><td class="mc">:</td><td>${endTimeStr}</td>
  </tr><tr>
    <td class="ml">Address</td><td class="mc">:</td><td colspan="4">${stationAddress || '&mdash;'}</td>
  </tr><tr>
    <td class="ml">Phone</td><td class="mc">:</td><td>${stationPhone || '&mdash;'}</td>
    <td class="ml">Terminal ID</td><td class="mc">:</td><td>${terminalId}</td>
  </tr><tr>
    <td class="ml">Nozzle / Pump</td><td class="mc">:</td><td>#${nozzle}</td>
    <td class="ml">Payment Method</td><td class="mc">:</td><td>BlueDiesel Digital Wallet</td>
  </tr>
</table>
${billToBlock}
<table class="items">
  <tr>
    <th class="c" style="width:28px">No.</th>
    <th>Description</th>
    <th class="c" style="width:85px">Volume (L)</th>
    <th class="r" style="width:115px">Unit Price (${currency}/L)</th>
    <th class="r" style="width:100px">Amount (${currency})</th>
  </tr>
  <tr>
    <td class="c">1</td>
    <td>${product}<br><span style="font-size:9pt;color:#555">Nozzle #${nozzle} &nbsp;|&nbsp; Duration: ${elapsedTime} &nbsp;|&nbsp; ${startTimeStr} &ndash; ${endTimeStr}</span></td>
    <td class="c">${volumeDispensed.toFixed(3)}</td>
    <td class="r">${unitPrice.toFixed(4)}</td>
    <td class="r">${subtotal.toFixed(2)}</td>
  </tr>
</table>
<table class="totals">
  <tr><td>Subtotal (excl. SST)</td><td class="r">${currency} ${subtotal.toFixed(2)}</td></tr>
  <tr><td>SST @ 8%</td><td class="r">${currency} ${sstAmount.toFixed(2)}</td></tr>
  <tr class="tr-total"><td>TOTAL PAYABLE</td><td class="r">${currency} ${total.toFixed(2)}</td></tr>
</table>
<div class="pay-box">
  <div class="pay-title">Payment Details</div>
  <div class="pay-row"><span>Payment Method</span><span>BlueDiesel Digital Wallet</span></div>
  <div class="pay-row"><span>Amount Paid</span><span>${currency} ${total.toFixed(2)}</span></div>
  <div class="pay-row"><span>Approval Code</span><span>${approvalCode}</span></div>
  <div class="pay-row"><span>Reference No.</span><span>${paymentRef}</span></div>
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
  };

  const handleShareReceipt = async () => {
    try {
      const { uri } = await Print.printToFileAsync({ html: generateReceiptHTML(), base64: false });
      const canShare = await Sharing.isAvailableAsync();
      if (canShare) {
        await Sharing.shareAsync(uri, { mimeType: 'application/pdf', dialogTitle: 'Share Receipt' });
      } else {
        Alert.alert('Sharing unavailable', 'Your device does not support file sharing.');
      }
    } catch (err: any) {
      Alert.alert('Error', err.message || 'Could not generate receipt PDF.');
    }
  };

  const handleEmailReceipt = async () => {
    try {
      const available = await MailComposer.isAvailableAsync();
      if (!available) { Alert.alert('Email unavailable', 'No mail app is configured on this device.'); return; }
      const { uri } = await Print.printToFileAsync({ html: generateReceiptHTML(), base64: false });
      await MailComposer.composeAsync({
        subject: `BlueDiesel Official Receipt - ${receiptNo}`,
        body: `Please find your official tax receipt attached.\n\nReceipt No.: ${receiptNo}\nStation: ${stationName}\nTotal Charged: ${currency} ${total.toFixed(2)}\n\nThank you for using BlueDiesel.`,
        attachments: [uri],
        recipients: (!isGuest && user?.email) ? [user.email] : [],
      });
    } catch (err: any) {
      Alert.alert('Error', err.message || 'Could not open mail composer.');
    }
  };

  return (
    <View style={[styles.root, { paddingTop: insets.top }]}>
      <View style={styles.navbar}>
        <TouchableOpacity style={styles.navBtn} onPress={() => router.push('./home')}>
          <Ionicons name="close" size={22} color="#000" />
        </TouchableOpacity>
        <Text style={styles.navTitle}>Official Tax Receipt</Text>
        <TouchableOpacity style={styles.navBtn} onPress={handleShareReceipt}>
          <Ionicons name="share-outline" size={22} color="#000" />
        </TouchableOpacity>
      </View>

      <ScrollView bounces={false} showsVerticalScrollIndicator={false}>
        <View style={styles.doc}>
          {/* Letterhead */}
          <View style={styles.letterhead}>
            <Text style={styles.coName}>BLUEDIESEL (M) SDN BHD</Text>
            <Text style={styles.coTag}>Portable Refilling Station Network</Text>
            <Text style={styles.coInfo}>info@bluediesel.com.my  |  bluediesel.com.my</Text>
            <Text style={styles.coInfo}>SST Reg. No.: {COMPANY_SST_REG}</Text>
          </View>

          {/* Title */}
          <View style={styles.docTitleBox}>
            <Text style={styles.docTitleText}>OFFICIAL TAX RECEIPT</Text>
          </View>

          {/* Meta */}
          <View style={styles.metaBlock}>
            {([
              ['Receipt No.', receiptNo,     'Date',        dateStr],
              ['Station',     stationName,   'Pump Start',  startTimeStr],
              ['Station ID',  stationId,     'Pump End',    endTimeStr],
              ['Address',     stationAddress || '\u2014', 'Terminal',   terminalId],
              ['Phone',       stationPhone || '\u2014',   'Nozzle / Pump', `#${nozzle}`],
              ['Payment',     'BlueDiesel Wallet', '',           ''],
            ] as [string, string, string, string][]).map(([l1, v1, l2, v2], i) => (
              <View key={i} style={styles.metaRow}>
                <Text style={styles.metaLabel}>{l1}</Text>
                <Text style={styles.metaColon}>:</Text>
                <Text style={[styles.metaValue, i === 0 && { fontWeight: '700' }]}>{v1}</Text>
                {l2 ? (
                  <>
                    <Text style={styles.metaLabel}>{l2}</Text>
                    <Text style={styles.metaColon}>:</Text>
                    <Text style={styles.metaValue}>{v2}</Text>
                  </>
                ) : <Text style={{ flex: 2 }} />}
              </View>
            ))}
          </View>

          {/* Bill To */}
          {!isGuest && user?.name && (
            <View style={styles.billBox}>
              <Text style={styles.billTitle}>BILL TO</Text>
              <Text style={styles.billRow}>{user.name}</Text>
              {user.email ? <Text style={styles.billRow}>{user.email}</Text> : null}
            </View>
          )}

          {/* Items table */}
          <View style={styles.tableWrap}>
            <View style={styles.tableHeader}>
              <Text style={[styles.th, { flex: 3 }]}>Description</Text>
              <Text style={[styles.th, styles.tCenter, { flex: 1 }]}>Vol. (L)</Text>
              <Text style={[styles.th, styles.tRight, { flex: 1.2 }]}>Unit Price</Text>
              <Text style={[styles.th, styles.tRight, { flex: 1.2 }]}>Amt ({currency})</Text>
            </View>
            <View style={styles.tableRow}>
              <View style={{ flex: 3, borderRightWidth: 1, borderRightColor: '#000' }}>
                <Text style={styles.td}>{product}</Text>
                <Text style={styles.tdSub}>{startTimeStr} – {endTimeStr}  |  Duration: {elapsedTime}</Text>
              </View>
              <Text style={[styles.td, styles.tCenter, { flex: 1 }]}>{volumeDispensed.toFixed(3)}</Text>
              <Text style={[styles.td, styles.tRight, { flex: 1.2 }]}>{unitPrice.toFixed(4)}</Text>
              <Text style={[styles.td, styles.tRight, { flex: 1.2 }]}>{subtotal.toFixed(2)}</Text>
            </View>
          </View>

          {/* Totals */}
          <View style={styles.totalsWrap}>
            <View style={styles.totalsRow}>
              <Text style={styles.totalsLabel}>Subtotal (excl. SST)</Text>
              <Text style={styles.totalsVal}>{currency} {subtotal.toFixed(2)}</Text>
            </View>
            <View style={styles.totalsRow}>
              <Text style={styles.totalsLabel}>SST @ 8%</Text>
              <Text style={styles.totalsVal}>{currency} {sstAmount.toFixed(2)}</Text>
            </View>
            <View style={styles.totalsFinalRow}>
              <Text style={styles.totalsFinalLabel}>TOTAL PAYABLE</Text>
              <Text style={styles.totalsFinalVal}>{currency} {total.toFixed(2)}</Text>
            </View>
          </View>

          {/* Payment */}
          <View style={styles.payBox}>
            <Text style={styles.payBoxTitle}>Payment Details</Text>
            <View style={styles.payRow}><Text style={styles.payBoxLabel}>Method</Text><Text style={styles.payBoxVal}>BlueDiesel Digital Wallet</Text></View>
            <View style={styles.payRow}><Text style={styles.payBoxLabel}>Amount Paid</Text><Text style={styles.payBoxVal}>{currency} {total.toFixed(2)}</Text></View>
            <View style={styles.payRow}><Text style={styles.payBoxLabel}>Approval Code</Text><Text style={styles.payBoxVal}>{approvalCode}</Text></View>
            <View style={styles.payRow}><Text style={styles.payBoxLabel}>Reference No.</Text><Text style={styles.payBoxVal}>{paymentRef}</Text></View>
            <View style={[styles.payRow, styles.payRowFinal]}><Text style={styles.payBoxLabel}>Status</Text><Text style={[styles.payBoxVal, { fontWeight: '700' }]}>APPROVED</Text></View>
          </View>

          <Text style={styles.eoe}>* Computer-generated receipt. No signature required. E. &amp; O.E.</Text>
        </View>

        {isGuest && (
          <View style={styles.guestBanner}>
            <Text style={styles.guestTitle}>Save Your Receipt History</Text>
            <Text style={styles.guestText}>Create an account to track your refill history and manage your wallet.</Text>
            <TouchableOpacity style={styles.guestBtn} onPress={() => router.push('./create-account')}>
              <Text style={styles.guestBtnText}>Create Account</Text>
              <Ionicons name="arrow-forward" size={14} color="#000" />
            </TouchableOpacity>
          </View>
        )}

        <View style={styles.actions}>
          <TouchableOpacity style={styles.actionRow} onPress={handleShareReceipt}>
            <Ionicons name="document-text-outline" size={18} color="#000" />
            <Text style={styles.actionRowText}>View / Download Invoice (PDF)</Text>
            <Ionicons name="chevron-forward" size={18} color="#555" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionRow} onPress={handleEmailReceipt}>
            <Ionicons name="mail-outline" size={18} color="#000" />
            <Text style={styles.actionRowText}>Email Receipt</Text>
            <Ionicons name="chevron-forward" size={18} color="#555" />
          </TouchableOpacity>
          {!isGuest && (
            <TouchableOpacity style={styles.actionRow} onPress={() => router.push('./transaction-history' as any)}>
              <Ionicons name="time-outline" size={18} color="#000" />
              <Text style={styles.actionRowText}>Transaction History</Text>
              <Ionicons name="chevron-forward" size={18} color="#555" />
            </TouchableOpacity>
          )}
        </View>

        <View style={{ height: insets.bottom + 32 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
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
  payBoxTitle: { fontSize: 10, fontWeight: '700', color: '#000', marginBottom: 6 },
  payBoxRow: { fontSize: 10, color: '#000', marginBottom: 2 },
  payRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 3 },
  payRowFinal: { marginTop: 5, paddingTop: 5, borderTopWidth: 1, borderTopColor: '#ccc' },
  payBoxLabel: { fontSize: 10, color: '#444' },
  payBoxVal: { fontSize: 10, color: '#000' },

  eoe: { fontSize: 9, fontStyle: 'italic', color: '#555', marginTop: 6 },

  guestBanner: { marginHorizontal: 16, marginBottom: 8, backgroundColor: '#fff', borderWidth: 1, borderColor: '#bbb', padding: 16 },
  guestTitle: { fontSize: 13, fontWeight: '700', color: '#000', marginBottom: 5 },
  guestText: { fontSize: 12, color: '#555', lineHeight: 17, marginBottom: 10 },
  guestBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 6, borderWidth: 1, borderColor: '#000', paddingVertical: 9 },
  guestBtnText: { fontSize: 13, fontWeight: '600', color: '#000' },

  actions: { paddingHorizontal: 16, backgroundColor: '#fff', borderTopWidth: 1, borderTopColor: '#ddd', marginTop: 8 },
  actionRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 15, borderBottomWidth: 1, borderBottomColor: '#eee', gap: 12 },
  actionRowText: { flex: 1, fontSize: 14, fontWeight: '500', color: '#000' },
});
