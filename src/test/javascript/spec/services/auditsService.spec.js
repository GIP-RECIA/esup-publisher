import AuditsService from '@/services/admin/AuditsService.js';
import FetchWrapper from '@/services/util/FetchWrapper.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/util/FetchWrapper.js');

describe('AuditsService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1 AuditsService - findAll', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    AuditsService.findAll().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/audits/all');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 2 AuditsService - findByDates', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const fromDate = new Date(2022, 0, 1, 0, 0, 0);
    const toDate = new Date(2022, 0, 31, 0, 0, 0);
    AuditsService.findByDates(fromDate, toDate).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        'api/audits/byDates?fromDate=2022-01-01T00%3A00%3A00.000Z&toDate=2022-01-31T00%3A00%3A00.000Z',
      );
      expect(value).toStrictEqual(response);
    });
  });
});
