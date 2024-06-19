import UserService from '@/services/user/UserService.js';
import FetchWrapper from '@/services/util/FetchWrapper.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/util/FetchWrapper.js');

// Tests unitaires sur le service UserService
describe('UserService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1 UserService - details function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    UserService.details('login').then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/extended/login');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 2 UserService - details function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    UserService.details('login').catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/extended/login');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 3 UserService - attributes function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    UserService.attributes().then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/attributes');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 4 UserService - attributes function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    UserService.attributes().catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/attributes');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 5 UserService - funtionalAttributes function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    UserService.funtionalAttributes().then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/fnattributes');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 6 UserService - funtionalAttributes function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    UserService.funtionalAttributes().catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/fnattributes');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 7 UserService - canCreateInCtx function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));
    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canCreateInCtx(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/createin?keyId=' + keyId + '&keyType=' + keyType);
      expect(result).toStrictEqual(response);
    });
  });

  it('test 8 UserService - canCreateInCtx function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));
    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canCreateInCtx(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/createin?keyId=' + keyId + '&keyType=' + keyType);
      expect(error).toStrictEqual(response);
    });
  });

  it('test 9 UserService - canEditCtx function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));
    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canEditCtx(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/edit?keyId=' + keyId + '&keyType=' + keyType);
      expect(result).toStrictEqual(response);
    });
  });

  it('test 10 UserService - canEditCtx function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));
    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canEditCtx(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/edit?keyId=' + keyId + '&keyType=' + keyType);
      expect(error).toStrictEqual(response);
    });
  });

  it('test 11 UserService - canDeleteCtx function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canDeleteCtx(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/delete?keyId=' + keyId + '&keyType=' + keyType);
      expect(result).toStrictEqual(response);
    });
  });

  it('test 12 UserService - canDeleteCtx function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canDeleteCtx(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/delete?keyId=' + keyId + '&keyType=' + keyType);
      expect(error).toStrictEqual(response);
    });
  });

  it('test 13 UserService - canEditCtxPerms function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));
    const keyId = 1;
    const keyType = 'ORGANIZATION';
    UserService.canEditCtxPerms(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editPerms?keyId=1&keyType=ORGANIZATION');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 14 UserService - canEditCtxPerms function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));
    const keyId = 1;
    const keyType = 'ORGANIZATION';
    UserService.canEditCtxPerms(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editPerms?keyId=1&keyType=ORGANIZATION');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 15 UserService - canEditCtxTargets function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));
    const keyId = 1;
    const keyType = 'ORGANIZATION';
    UserService.canEditCtxTargets(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editTargets?keyId=1&keyType=ORGANIZATION');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 16 UserService - canEditCtxTargets function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));
    const keyId = 1;
    const keyType = 'ORGANIZATION';
    UserService.canEditCtxTargets(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editTargets?keyId=1&keyType=ORGANIZATION');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 17 UserService - canModerateAnyThing function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    UserService.canModerateAnyThing().then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/moderate');
      expect(result).toStrictEqual(response);
    });
  });

  it('test 18 UserService - canModerateAnyThing function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    UserService.canModerateAnyThing().catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/moderate');
      expect(error).toStrictEqual(response);
    });
  });

  it('test 19 UserService - canHighlight function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canHighlight(keyId, keyType).then((result) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/highlight?keyId=' + keyId + '&keyType=' + keyType);
      expect(result).toStrictEqual(response);
    });
  });

  it('test 20 UserService - canHighlight function KO', () => {
    const response = {};
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.reject(response));

    const keyId = 1;
    const keyType = 'CATEGORY';
    UserService.canHighlight(keyId, keyType).catch((error) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/highlight?keyId=' + keyId + '&keyType=' + keyType);
      expect(error).toStrictEqual(response);
    });
  });

  it('test 21 UserService - search function OK', () => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.postJson = vi.fn().mockReturnValue(Promise.resolve(response));

    const data = {
      criteria: {
        color: 'white',
      },
    };
    UserService.search(data).then((result) => {
      expect(FetchWrapper.postJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.postJson).toHaveBeenCalledWith('api/users/search', data);
      expect(result).toStrictEqual(response);
    });
  });

  it('test 22 UserService - search function KO', () => {
    const response = {};
    FetchWrapper.postJson = vi.fn().mockReturnValue(Promise.reject(response));

    const data = {
      criteria: {
        color: 'white',
      },
    };
    UserService.search(data).catch((error) => {
      expect(FetchWrapper.postJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.postJson).toHaveBeenCalledWith('api/users/search', data);
      expect(error).toStrictEqual(response);
    });
  });
});
