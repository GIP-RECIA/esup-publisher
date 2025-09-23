import GroupService from '@/services/entities/group/GroupService.js';
import FetchWrapper from '@/services/util/FetchWrapper.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/util/FetchWrapper.js');

describe('GroupService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1 GroupService - query without params', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    GroupService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 2 GroupService - query with params', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    GroupService.query({ key: 'value' }).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups?key=value');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 3 GroupService - details', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const id = 1;
    GroupService.details(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/groups/extended/${id}`);
      expect(value).toStrictEqual(response);
    });
  });

  it('test 4 GroupService - attribute', () => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    GroupService.attributes().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups/attributes');
      expect(value).toStrictEqual(response);
    });
  });

  it('test 5 GroupService - userMembers', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const id = 1;
    GroupService.userMembers(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/groups/usermembers?id=${id}`);
      expect(value).toStrictEqual(response);
    });
  });
});
