import { shallowMount, RouterLinkStub, flushPromises } from '@vue/test-utils';
import Organization from '@/views/entities/organization/Organization.vue';
import OrganizationService from '@/services/entities/organization/OrganizationService.js';
import { beforeEach, describe, expect, it, vi } from 'vitest';

vi.mock('@/services/entities/enum/EnumDatasService.js');

// Tests unitaires sur la page Owned
describe('Organization.vue tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("test 1 Organization - Affichage d'un élément dans la liste des organizations", async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      }),
    );

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };

    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(1);
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(1);
  });

  it("test 2 Organization - Affichage d'éléments dans la liste des organizations", async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    const organization2 = {
      id: 2,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Lycée De L'Iroise",
      displayName: "Lycée De L'Iroise",
      description: "Contexte de publication : Lycée De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };
    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1, organization2]),
      }),
    );

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(2);
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(2);
  });

  it('test 3 Organization - createOrganization', async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    OrganizationService.query = vi
      .fn()
      .mockReturnValueOnce(
        Promise.resolve({
          data: [],
        }),
      )
      .mockReturnValue(
        Promise.resolve({
          data: Object.assign([], [organization1]),
        }),
      );

    OrganizationService.update = vi.fn().mockReturnValue(Promise.resolve({}));
    OrganizationService.delete = vi.fn().mockReturnValue(Promise.resolve({}));

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(0);
    await wrapper.vm.createOrganization();

    await flushPromises();

    expect(OrganizationService.update).toHaveBeenCalledTimes(1);
    expect(OrganizationService.query).toHaveBeenCalledTimes(2);
    expect(wrapper.vm.organizations.length).toStrictEqual(1);
  });

  it('test 4 Organization - organizationDetail', async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      }),
    );

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $router = {
      push: vi.fn(),
    };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
          $router,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(1);
    await wrapper.vm.organizationDetail(organization1.id);
    expect($router.push).toHaveBeenCalledTimes(1);
    expect($router.push).toHaveBeenCalledWith({
      name: 'AdminEntityOrganizationDetails',
      params: { id: organization1.id },
    });
  });

  it('test 5 Organization - update', async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      }),
    );

    OrganizationService.get = vi.fn().mockReturnValue(Promise.resolve({ data: organization1 }));

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(1);
    await wrapper.vm.update(organization1.id);
    expect(OrganizationService.get).toHaveBeenCalledTimes(1);
  });

  it('test 5 Organization - deleteOrganization', async () => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: 'admin',
        displayName: 'Administrator',
        enabled: true,
        acceptNotifications: false,
        email: '',
        langKey: 'fr',
        createdDate: '2020-01-30T15:02:04Z',
        lastModifiedDate: null,
        subject: { keyId: 'admin', keyType: 'PERSON' },
      },
      createdDate: '2017-03-01T07:00:00Z',
      lastModifiedDate: '2017-03-01T07:00:00Z',
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ['0291595B', '19291595700018'],
    };

    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      }),
    );

    OrganizationService.get = vi.fn().mockReturnValue(Promise.resolve({ data: organization1 }));
    OrganizationService.delete = vi.fn().mockReturnValue(Promise.resolve({}));

    const $t = (param) => param;
    const $route = { params: { itemState: '' } };
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          'has-any-role': {},
        },
      },
    });

    await flushPromises();

    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(wrapper.vm.organizations.length).toStrictEqual(1);
    await wrapper.vm.deleteOrganization(organization1.id);

    await flushPromises();

    expect(OrganizationService.get).toHaveBeenCalledTimes(1);
    await wrapper.vm.confirmDelete(organization1.id);

    await flushPromises();

    expect(OrganizationService.delete).toHaveBeenCalledTimes(1);
    setTimeout(() => {
      expect(wrapper.vm.organizations.length).toStrictEqual(0);
    }, 200);
  });
});
