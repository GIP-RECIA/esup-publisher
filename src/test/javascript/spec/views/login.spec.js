import { flushPromises, shallowMount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import AuthenticationService from '@/services/auth/AuthenticationService.js'
import store from '@/store/index.js'
import Login from '@/views/account/login/Login.vue'

// Tests unitaires sur la page de Login
describe('login.vue tests', () => {
  it('test 1 Login - Modal ouverte', async () => {
    store.setLoginModalOpened(true)
    AuthenticationService.logout = vi.fn().mockReturnValue(Promise.resolve({}))
    const $t = param => param
    const wrapper = shallowMount(Login, {
      global: {
        mocks: {
          $t,
        },
      },
    })

    await flushPromises()

    expect(store.getLoginModalOpened).toBe(true)
    expect(wrapper.find('#login-modal').exists()).toBe(true)
    expect(AuthenticationService.logout).toHaveBeenCalledTimes(1)
  })

  it('test 2 Login - Modal fermée', async () => {
    store.setLoginModalOpened(false)

    AuthenticationService.logout = vi.fn().mockReturnValue(Promise.resolve({}))
    const $t = param => param
    const wrapper = shallowMount(Login, {
      global: {
        mocks: {
          $t,
        },
      },
    })

    await flushPromises()

    expect(store.getLoginModalOpened).toBe(false)
    expect(wrapper.find('button').exists()).toBe(true)
    expect(wrapper.get('#login-button').text()).toMatch('login.form.button')
    expect(AuthenticationService.logout).toHaveBeenCalledTimes(0)
  })
})
