// Mock for expo-constants
const Constants = {
  expoConfig: {
    extra: {
      API_BASE_URL: 'https://api.test.interion.com',
      WS_API_URL: 'wss://ws.test.interion.com',
    },
  },
};

export default Constants;
module.exports = { default: Constants, ...Constants };
