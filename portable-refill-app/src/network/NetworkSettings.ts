export type ProtocolSecurityType = 'HTTP' | 'HTTPS';

export interface NetworkSettings {
  host: string;

  protocol: ProtocolSecurityType;

  httpPort: number;
  httpsPort: number;

  useAuthentication: boolean;
  login: string;
  password: string;
}

// defaults (similar to Android mSettings)
export const defaultNetworkSettings: NetworkSettings = {
  host: '192.168.1.100',

  protocol: 'HTTP',

  httpPort: 8080,
  httpsPort: 8443,

  useAuthentication: false,
  login: '',
  password: '',
};
