import { describe, expect, it } from 'vitest'
import Base64Utils from '@/services/util/Base64Utils.js'

// Tests unitaires sur la classe utilitaire Base64Utils
describe('base64Utils.js tests', () => {
  it('test 1 Base64Utils - encode/decode', () => {
    const value = 'testString'
    expect(Base64Utils.decode(Base64Utils.encode(value))).toStrictEqual(value)
  })

  it('test 2 Base64Utils - decode/encode', () => {
    const value = 'ZmlsZXMvNDlcMjAyMjAxXDIwMjIwMTIzMTQzNDA2LmpwZw=='
    expect(Base64Utils.encode(Base64Utils.decode(value))).toStrictEqual(value)
  })
})
