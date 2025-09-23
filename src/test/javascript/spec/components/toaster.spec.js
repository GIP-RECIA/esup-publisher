import { shallowMount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import Toaster from '@/components/toaster/Toaster.vue'

// Tests unitaires du composant Toaster
describe('toaster.vue tests', () => {
  it('test 1 Toaster - display/dismiss toast', () => {
    // Mock des parentTop et parentBottom car jdom ne gère pas la méthode insertAdjacentElement
    const parentTop = document.createElement('div')
    parentTop.className = 'custom-toast-container custom-toast-top'
    parentTop.insertAdjacentElement = (arg1, arg2) => parentTop.appendChild(arg2)
    const parentBottom = document.createElement('div')
    parentBottom.className = 'custom-toast-container custom-toast-bottom'
    parentBottom.insertAdjacentElement = (arg1, arg2) => parentBottom.appendChild(arg2)
    document.body.appendChild(parentTop)
    document.body.appendChild(parentBottom)

    const wrapper = shallowMount(Toaster, {
      props: {
        message: 'my toast message',
        type: 'warning',
        position: 'bottom',
        duration: 60000,
        queue: false,
        pauseOnHover: false,
      },
      data() {
        return {
          parentTop,
          parentBottom,
        }
      },
    })

    expect(wrapper.vm.isActive).toBe(true)
    expect(parentBottom.querySelectorAll('.custom-toast-item-warning.custom-toast-item-bottom').length).toStrictEqual(1)
    expect(parentBottom.querySelector('.custom-toast-text').textContent).toStrictEqual('my toast message')

    parentBottom.querySelectorAll('.custom-toast-item-warning.custom-toast-item-bottom')[0].click()
    expect(wrapper.vm.isActive).toBe(false)
    setTimeout(() => {
      expect(parentBottom.querySelectorAll('.custom-toast-item-warning.custom-toast-item-bottom').length).toStrictEqual(0)
    }, 200)
  })
})
