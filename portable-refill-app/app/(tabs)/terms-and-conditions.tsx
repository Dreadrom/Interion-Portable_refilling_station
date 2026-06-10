import { router } from 'expo-router';
import { ScrollView, Text, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';

export default function TermsAndConditionsScreen() {
  return (
    <View style={{ flex: 1, backgroundColor: '#F8FAFC' }}>
      <ScrollView contentContainerStyle={[globalStyles.container, { flexGrow: 1, paddingBottom: 80 }]}>
        <Text style={globalStyles.title}>MY Diesel</Text>
        <Text style={globalStyles.subtitle}>Terms and Conditions</Text>
        
        <View style={{ marginTop: 20, marginBottom: 20 }}>
          <Text style={{ fontSize: 12, color: '#64748B', marginBottom: 10 }}>
            Last Updated: June 2, 2026
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            1. Acceptance of Terms
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            By creating an account and using MY Diesel mobile application ("App"), you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please do not use the App.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            2. Wallet System
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            2.1. You must maintain a positive wallet balance to purchase AdBlue at MY Diesel stations.{'\n\n'}
            2.2. Wallet top-ups are processed through Fiuu payment gateway and are non-refundable except as required by law.{'\n\n'}
            2.3. Any unused balance in your wallet will remain available for future purchases.{'\n\n'}
            2.4. We reserve the right to refund partial amounts if you pre-authorize more than you dispense.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            3. Fuel Dispensing
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            3.1. You must scan the station QR code and authorize the transaction before dispensing begins.{'\n\n'}
            3.2. You will be charged only for the actual amount dispensed.{'\n\n'}
            3.3. If you authorize more than you dispense, the difference will be refunded to your wallet within 24 hours.{'\n\n'}
            3.4. Emergency stop buttons are available at all stations for your safety.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            4. Payment and Billing
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            4.1. All payments are processed securely through Fiuu payment gateway.{'\n\n'}
            4.2. You are responsible for maintaining the security of your payment information.{'\n\n'}
            4.3. Transaction receipts will be available in the App immediately after purchase.{'\n\n'}
            4.4. Prices are subject to change without prior notice.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            5. User Account
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            5.1. You are responsible for maintaining the confidentiality of your account credentials.{'\n\n'}
            5.2. You must notify us immediately of any unauthorized use of your account.{'\n\n'}
            5.3. You must provide accurate and complete information when creating your account.{'\n\n'}
            5.4. We reserve the right to suspend or terminate accounts that violate these terms.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            6. Privacy and Data Protection
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            6.1. We collect and process your personal data in accordance with our Privacy Policy.{'\n\n'}
            6.2. Your payment information is encrypted and securely stored.{'\n\n'}
            6.3. We may use your location data to show nearby stations (with your permission).{'\n\n'}
            6.4. Transaction history is stored for your convenience and our record-keeping.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            7. Refunds and Cancellations
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            7.1. Wallet top-ups are generally non-refundable.{'\n\n'}
            7.2. If you dispense less fuel than authorized, the difference will be refunded to your wallet.{'\n\n'}
            7.3. Refunds to your original payment method may take 5-14 business days depending on your bank.{'\n\n'}
            7.4. You may request a full refund of your wallet balance by contacting customer support.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            8. Limitation of Liability
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            8.1. We are not responsible for any loss or damage arising from your use of the App.{'\n\n'}
            8.2. We do not guarantee uninterrupted or error-free service.{'\n\n'}
            8.3. Our total liability to you shall not exceed the amount in your wallet balance.{'\n\n'}
            8.4. We are not liable for any network connectivity issues or payment gateway failures.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            9. Prohibited Activities
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            You agree NOT to:{'\n\n'}
            • Use the App for any illegal purposes{'\n'}
            • Attempt to hack or compromise the App security{'\n'}
            • Share your account credentials with others{'\n'}
            • Manipulate or tamper with fuel dispensing equipment{'\n'}
            • Create multiple accounts to abuse promotions{'\n'}
            • Use automated tools or bots with the App
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            10. Changes to Terms
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            We reserve the right to modify these Terms and Conditions at any time. Changes will be effective immediately upon posting in the App. Your continued use of the App after changes constitutes acceptance of the modified terms.
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            11. Contact Information
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 15 }}>
            For questions about these Terms and Conditions, please contact us at:{'\n\n'}
            Email: support@mydiesel.com{'\n'}
            Phone: +60 3-XXXX XXXX{'\n'}
            Address: MY Diesel Headquarters, Malaysia
          </Text>

          <Text style={{ fontSize: 16, fontWeight: '600', color: '#1E293B', marginTop: 20, marginBottom: 10 }}>
            12. Governing Law
          </Text>
          <Text style={{ fontSize: 14, color: '#475569', lineHeight: 22, marginBottom: 20 }}>
            These Terms and Conditions are governed by the laws of Malaysia. Any disputes shall be resolved in the courts of Malaysia.
          </Text>
        </View>
      </ScrollView>

      <View style={{ 
        position: 'absolute', 
        bottom: 0, 
        left: 0, 
        right: 0, 
        backgroundColor: '#F8FAFC',
        borderTopWidth: 1,
        borderTopColor: '#E2E8F0',
        padding: 20
      }}>
        <TouchableOpacity
          style={globalStyles.primaryButton}
          onPress={() => router.back()}
        >
          <Text style={globalStyles.primaryButtonText}>Back</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}
