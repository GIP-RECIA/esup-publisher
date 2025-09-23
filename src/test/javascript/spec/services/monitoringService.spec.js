import MonitoringService from '@/services/admin/MonitoringService.js';
import FetchWrapper from '@/services/util/FetchWrapper.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/util/FetchWrapper.js');

describe('MonitoringService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1 MonitoringService - getMetrics', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.getMetrics().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('management/jhimetrics');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 2 MonitoringService - checkHealth', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.checkHealth().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('management/health');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 3 MonitoringService - threadDump', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.threadDump().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('management/threaddump');
      expect(value).toStrictEqual(response);
    });
  });
});
