import ConfigurationService from '@/services/admin/ConfigurationService.js';
import FetchWrapper from '@/services/util/FetchWrapper.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/util/FetchWrapper.js');

describe('ConfigurationService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1 ConfigurationService - get', () => {
    const response = {
      data: {
        key1: 'val1',
        key2: 'val2',
      },
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    ConfigurationService.get().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('management/configprops');
      expect(value).toStrictEqual(['val1', 'val2']);
    });
  });

  it('test 2 ConfigurationService - getEnv', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    ConfigurationService.getEnv().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('management/env');
      expect(value).toStrictEqual(response);
    });
  });
});
