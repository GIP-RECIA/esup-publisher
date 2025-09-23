import { flushPromises, shallowMount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import Spinner from '@/components/spinner/Spinner.vue'
import FetchWrapper from '@/services/util/FetchWrapper.js'

// Tests unitaires du composant Spinner
describe('spinner.vue tests', () => {
  it('test 1 Spinner - showSpinner', async () => {
    FetchWrapper.countPendingRequests = vi.fn().mockReturnValue(0)
    const $t = param => param
    const wrapper = shallowMount(Spinner, {
      global: {
        mocks: {
          $t,
        },
      },
    })

    await flushPromises()
    expect(wrapper.find('.spinner-border').exists()).toBe(false)
    expect(wrapper.vm.showSpinner).toStrictEqual(false)
    wrapper.vm.countPendingRequests = 2
    await flushPromises()
    expect(wrapper.find('.spinner-border').exists()).toBe(true)
    expect(wrapper.vm.showSpinner).toStrictEqual(true)
  })
})
