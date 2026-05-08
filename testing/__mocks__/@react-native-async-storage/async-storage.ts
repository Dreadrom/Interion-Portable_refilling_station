// Mock for @react-native-async-storage/async-storage
const db: Record<string, string> = {};

const AsyncStorage = {
  getItem: jest.fn((key: string) => Promise.resolve(db[key] ?? null)),
  setItem: jest.fn((key: string, value: string) => {
    db[key] = value;
    return Promise.resolve();
  }),
  removeItem: jest.fn((key: string) => {
    delete db[key];
    return Promise.resolve();
  }),
  clear: jest.fn(() => {
    Object.keys(db).forEach((k) => delete db[k]);
    return Promise.resolve();
  }),
  getAllKeys: jest.fn(() => Promise.resolve(Object.keys(db))),
  multiGet: jest.fn((keys: string[]) =>
    Promise.resolve(keys.map((k) => [k, db[k] ?? null] as [string, string | null]))
  ),
  multiSet: jest.fn((pairs: [string, string][]) => {
    pairs.forEach(([k, v]) => { db[k] = v; });
    return Promise.resolve();
  }),
  multiRemove: jest.fn((keys: string[]) => {
    keys.forEach((k) => delete db[k]);
    return Promise.resolve();
  }),
};

export default AsyncStorage;
