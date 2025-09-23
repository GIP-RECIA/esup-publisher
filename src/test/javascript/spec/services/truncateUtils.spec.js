import TruncateUtils from '@/services/util/TruncateUtils.js';
import { describe, expect, it } from 'vitest';

describe('TruncateUtils.js tests', () => {
  it('test 1 TruncateUtils - characters withouy breakOnWord', () => {
    let result = TruncateUtils.characters('Phrase de test', 8, false);
    let expectResult = 'Phrase...';
    expect(result).toStrictEqual(expectResult);
  });

  it('test 2 TruncateUtils - characters with breakOnWord', () => {
    let result = TruncateUtils.characters('Phrase de test', 8, true);
    let expectResult = 'Phrase d...';
    expect(result).toStrictEqual(expectResult);
  });

  it('test 3 TruncateUtils - characters no truncate', () => {
    let result = TruncateUtils.characters('Phrase', 8, true);
    let expectResult = 'Phrase';
    expect(result).toStrictEqual(expectResult);
  });

  it('test 4 TruncateUtils - words', () => {
    let result = TruncateUtils.words('Phrase de test', 2);
    let expectResult = 'Phrase de...';
    expect(result).toStrictEqual(expectResult);
  });

  it('test 5 TruncateUtils - words no truncate', () => {
    let result = TruncateUtils.words('Phrase de test', 8, true);
    let expectResult = 'Phrase de test';
    expect(result).toStrictEqual(expectResult);
  });
});
