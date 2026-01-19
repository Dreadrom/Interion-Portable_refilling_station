import { StyleSheet } from 'react-native';

export const globalStyles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: '700',
    textAlign: 'center',
    marginTop: 40,
  },
  subtitle: {
    textAlign: 'center',
    marginBottom: 30,
    fontWeight: '600',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    padding: 14,
    marginBottom: 14,
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
  },
  primaryButton: { 
    backgroundColor: '#000', 
    padding: 16, 
    borderRadius: 8, 
    marginTop: 10, 
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
    marginBottom: 12,
}, 
  primaryButtonText: { 
    color: '#fff', 
    textAlign: 'center', 
    fontWeight: '600', 
},
  secondaryButton: {
    backgroundColor: '#eee',
    padding: 16,
    borderRadius: 10,
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
    marginBottom: 12,
    alignItems: 'center',
  },
  secondaryButtonText: {
    textAlign: 'center',
    fontWeight: '600',
  },
  or: {
    textAlign: 'center',
    marginVertical: 12,
    color: '#999',
  },
  resend: {
    textAlign: 'center',
    color: '#999',
    marginBottom: 20,
  },
  logo: {
  marginTop: 60,
  width: 100,
  height: 100,
  marginBottom: 20,
  alignSelf: 'center',
},
});
