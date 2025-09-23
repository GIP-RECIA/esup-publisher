import Base64Utils from '@/services/util/Base64Utils.js';
import { describe, expect, it } from 'vitest';

// Tests unitaires sur la classe utilitaire Base64Utils
describe('Base64Utils.js tests', () => {
  it('test 1 Base64Utils - encode/decode', () => {
    let value = 'testString';
    expect(Base64Utils.decode(Base64Utils.encode(value))).toStrictEqual(value);
  });

  it('test 2 Base64Utils - decode/encode', () => {
    let value = 'ZmlsZXMvNDlcMjAyMjAxXDIwMjIwMTIzMTQzNDA2LmpwZw==';
    expect(Base64Utils.encode(Base64Utils.decode(value))).toStrictEqual(value);
  });
});
