import ContextService from '@/services/entities/context/ContextService.js'
import Treeview from '@/views/manager/treeview/Treeview.vue'
import { flushPromises, shallowMount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'

// Tests unitaires sur la page Treeview
describe('treeview.vue tests', () => {
  let wrapper
  let $router
  let datas

  beforeEach(() => {
    datas = [
      {
        id: '1:PUBLISHER',
        type: 'PUBLISHER',
        children: true,
        parent: {
          id: '1:ORGANIZATION',
        },
      },
    ]

    ContextService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: datas,
      }),
    )

    const $t = param => param

    const $route = {
      fullPath: 'fullPath',
    }
    $router = {
      push: vi.fn(),
    }

    wrapper = shallowMount(Treeview, {
      global: {
        stubs: {
          EsupJsTree: { template: '<div class="esup-js-tree-stub"></div>' },
          RouterView: { template: '<div class="router-view-stub"></div>' },
        },
        mocks: {
          $t,
          $route,
          $router,
        },
      },
    })
  })

  it('test 1 Treeview - Initialisation', async () => {
    await flushPromises()
    setTimeout(() => {
      expect(wrapper.find('.esup-js-tree-stub').exists()).toBe(true)
    }, 200)
    expect(wrapper.vm.treeData.length).toStrictEqual(1)
    expect(wrapper.vm.treeData[0].id).toStrictEqual(datas[0].id)
    expect(wrapper.vm.treeData[0].getChildren).toBeDefined()
    expect(wrapper.vm.treeData[0].iconIndex).toStrictEqual(1)
    expect(wrapper.vm.isDataLoad).toStrictEqual(true)
    expect(ContextService.query).toHaveBeenCalledTimes(1)
    expect(ContextService.query).toHaveBeenCalledWith(1)
  })

  it('test 2 Treeview - onTreeSelection', async () => {
    await flushPromises()

    wrapper.vm.onTreeSelection(datas)
    expect(wrapper.vm.parentNodeId).toStrictEqual(datas[0].parent.id)
    expect($router.push).toHaveBeenCalledTimes(1)
    expect($router.push).toHaveBeenCalledWith({
      name: 'TreeviewCtxDetails',
      params: { ctxId: '1', ctxType: 'PUBLISHER' },
    })
  })

  it('test 3 Treeview - getIconIndexByType', async () => {
    await flushPromises()

    expect(wrapper.vm.getIconIndexByType('ORGANIZATION')).toStrictEqual(0)
    expect(wrapper.vm.getIconIndexByType('PUBLISHER')).toStrictEqual(1)
    expect(wrapper.vm.getIconIndexByType('CATEGORY')).toStrictEqual(2)
    expect(wrapper.vm.getIconIndexByType('FEED')).toStrictEqual(3)
    expect(wrapper.vm.getIconIndexByType('ITEM')).toStrictEqual(4)
    expect(wrapper.vm.getIconIndexByType('test')).toStrictEqual(null)
  })
})
