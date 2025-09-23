import { flushPromises, RouterLinkStub, shallowMount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import App from '@/App.vue'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import ConfigurationService from '@/services/params/ConfigurationService.js'

vi.mock('@/services/entities/enum/EnumDatasService.js')

// Tests unitaires sur la page App
describe('app.vue tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 App footer - Affichage de la version de l\'application', async () => {
    ConfigurationService.init = vi.fn().mockReturnValue(Promise.resolve([]))
    EnumDatasService.init = vi.fn().mockReturnValue(Promise.resolve([]))
    process.env = Object.assign(process.env, {
      NODE_ENV: 'production',
      BACK_VERSION: '0.5.14',
    })
    const $t = param => param
    const $router = {
      currentRoute: {
        value: {
          meta: {
            cssClass: 'site',
          },
        },
      },
    }
    const $store = {
      commit: vi.fn(),
      getters: {
        getLanguage: 'fr',
      },
    }
    const $i18n = {
      locale: 'en',
    }
    const wrapper = shallowMount(App, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: { template: '<div class="router-view-stub"></div>' },
        },
        mocks: {
          $router,
          $t,
          $store,
          $i18n,
        },
      },
    })

    await flushPromises()
    expect(wrapper.find('#footer-back-version').exists()).toBe(true)
    expect(wrapper.find('.development').exists()).toBe(false)
    expect(ConfigurationService.init).toHaveBeenCalledTimes(1)
    expect(EnumDatasService.init).toHaveBeenCalledTimes(1)
    expect($store.commit).toHaveBeenCalledTimes(1)
    expect($store.commit).toHaveBeenCalledWith('initializeStore')
    expect($i18n.locale).toBe('fr')
  })
})
