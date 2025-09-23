import { flushPromises, shallowMount } from '@vue/test-utils';
import AdminMain from '@/views/admin/main/AdminMain.vue';
import { describe, expect, it } from 'vitest';

// Tests unitaires sur la page AdminMain
describe('AdminMain.vue tests', () => {
  it('test 1 AdminMain - Affichage du titre', async () => {
    const $t = (param) => param;
    const wrapper = shallowMount(AdminMain, {
      global: {
        mocks: {
          $t,
        },
      },
    });

    await flushPromises();
    expect(wrapper.get('h1').text()).toMatch('admin.main.text');
  });
});
