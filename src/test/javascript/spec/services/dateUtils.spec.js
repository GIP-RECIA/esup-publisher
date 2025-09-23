import DateUtils from '@/services/util/DateUtils.js';
import { describe, expect, it } from 'vitest';

// Tests unitaires sur la classe utilitaire DateUtils
describe('DateUtils.js tests', () => {
  it('test 1 DateUtils - convertDateTimeFromServer function OK', () => {
    let result = DateUtils.convertDateTimeFromServer('09/12/2021');
    let expectResult = new Date('09/12/2021');
    expect(result).toStrictEqual(expectResult);
  });

  it('test 2 DateUtils - convertDateTimeFromServer function KO: no date provided', () => {
    let result = DateUtils.convertDateTimeFromServer();
    expect(result).toBe(null);
  });

  it('test 3 DateUtils - convertLocalDateFromServer function OK', () => {
    let result = DateUtils.convertLocalDateFromServer('2021-12-09');
    let expectResult = new Date('2021', '11', '09');
    expect(result).toStrictEqual(expectResult);
  });

  it('test 4 DateUtils - convertLocalDateFromServer function KO: no date provided', () => {
    let result = DateUtils.convertLocalDateFromServer();
    expect(result).toBe(null);
  });

  it('test 5 DateUtils - convertLocalDateToServer function OK', () => {
    let result = DateUtils.convertLocalDateToServer('2021-12-09');
    let expectResult = '2021-12-09';
    expect(result).toStrictEqual(expectResult);
  });

  it('test 6 DateUtils - convertLocalDateToServer function KO: no date provided', () => {
    let result = DateUtils.convertLocalDateToServer();
    expect(result).toBe(null);
  });

  it('test 7 DateUtils - addDaysToLocalDate function OK', () => {
    let result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'), 1);
    let expectResult = new Date('2021', '11', '10');
    expect(result).toStrictEqual(expectResult);
  });

  it('test 8 DateUtils - addDaysToLocalDate function KO: no date provided', () => {
    let result = DateUtils.addDaysToLocalDate();
    expect(result).toBe(null);
  });

  it('test 9 DateUtils - addDaysToLocalDate function KO: no nbDays provided', () => {
    let result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'));
    expect(result).toStrictEqual(null);
  });

  it('test 10 DateUtils - addDaysToLocalDate function KO: no valid nbDays format provided', () => {
    let result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'), '1');
    expect(result).toStrictEqual(null);
  });

  it('test 11 DateUtils - addDaysToLocalDate function KO: no valid date format provided', () => {
    let result = DateUtils.addDaysToLocalDate('2021-12-09', 1);
    expect(result).toStrictEqual(null);
  });

  it('test 12 DateUtils - convertToIntString function OK', () => {
    let result = DateUtils.convertToIntString(
      new Date(2021, 11, 9, 10, 55, 40),
      {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
      },
      'en',
    );
    // warning nodejs version have a change moving from whitespace to a non-breaking whitespace
    expect(result).toMatch(new RegExp('December 9, 2021 at 10:55:40\\sAM'));
  });

  it('test 13 DateUtils - min function OK', () => {
    let date1 = DateUtils.convertDateTimeFromServer('09/12/2021');
    let date2 = DateUtils.convertDateTimeFromServer('09/12/2022');
    let result = DateUtils.min(date1, date2);
    expect(result).toStrictEqual(date1);

    result = DateUtils.min(date1, null);
    expect(result).toStrictEqual(date1);

    result = DateUtils.min(null, date2);
    expect(result).toStrictEqual(date2);

    result = DateUtils.min(null, null);
    expect(result).toStrictEqual(null);
  });
});
