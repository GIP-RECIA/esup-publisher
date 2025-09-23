import { describe, expect, it } from 'vitest'
import TruncateUtils from '@/services/util/TruncateUtils.js'

describe('truncateUtils.js tests', () => {
  it('test 1 TruncateUtils - characters withouy breakOnWord', () => {
    const result = TruncateUtils.characters('Phrase de test', 8, false)
    const expectResult = 'Phrase...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 2 TruncateUtils - characters with breakOnWord', () => {
    const result = TruncateUtils.characters('Phrase de test', 8, true)
    const expectResult = 'Phrase d...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 3 TruncateUtils - characters no truncate', () => {
    const result = TruncateUtils.characters('Phrase', 8, true)
    const expectResult = 'Phrase'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 4 TruncateUtils - words', () => {
    const result = TruncateUtils.words('Phrase de test', 2)
    const expectResult = 'Phrase de...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 5 TruncateUtils - words no truncate', () => {
    const result = TruncateUtils.words('Phrase de test', 8, true)
    const expectResult = 'Phrase de test'
    expect(result).toStrictEqual(expectResult)
  })
})
