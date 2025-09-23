import { flushPromises, shallowMount } from '@vue/test-utils';
import Manager from '@/views/manager/Manager.vue';
import OrganizationService from '@/services/entities/organization/OrganizationService.js';
import RedactorService from '@/services/entities/redactor/RedactorService.js';
import SubjectService from '@/services/params/SubjectService.js';
import { describe, expect, it, vi } from 'vitest';

// Tests unitaires sur la page Manager
describe('Manager.vue tests', () => {
  it('test 1 Manager - Initialisation', async () => {
    const organizations = [
      {
        id: 1,
      },
    ];

    OrganizationService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: organizations,
      }),
    );

    const redactors = [
      {
        id: 1,
      },
    ];

    RedactorService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: redactors,
      }),
    );

    SubjectService.init = vi.fn().mockReturnValue(Promise.resolve([]));
    const $router = {
      currentRoute: {
        value: {
          meta: {
            managerCssClass: 'cssClass',
          },
        },
      },
    };

    const wrapper = shallowMount(Manager, {
      global: {
        stubs: {
          RouterView: { template: '<div class="router-view-stub"></div>' },
        },
        mocks: {
          $router,
        },
      },
    });

    await flushPromises();

    expect(wrapper.find('.manager.cssClass').exists()).toBe(true);
    expect(wrapper.vm.organizations).toStrictEqual(organizations);
    expect(wrapper.vm.redactors).toStrictEqual(organizations);
    expect(wrapper.vm.initData).toStrictEqual(true);
    expect(OrganizationService.query).toHaveBeenCalledTimes(1);
    expect(RedactorService.query).toHaveBeenCalledTimes(1);
    expect(SubjectService.init).toHaveBeenCalledTimes(1);
  });
});
