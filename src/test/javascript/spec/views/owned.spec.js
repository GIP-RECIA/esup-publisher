import { flushPromises, RouterLinkStub, shallowMount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import ClassificationService from '@/services/entities/classification/ClassificationService.js'
import ContentService from '@/services/entities/content/ContentService.js'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import ItemService from '@/services/entities/item/ItemService.js'
import DateUtils from '@/services/util/DateUtils.js'
import ParseLinkUtils from '@/services/util/ParseLinkUtils.js'
import Owned from '@/views/manager/contents/owned/Owned.vue'

vi.mock('@/services/entities/enum/EnumDatasService.js')

// Tests unitaires sur la page Owned
describe('owned.vue tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mock('bootstrap', () => ({
      Modal: class {
        show() {}
        hide() {}
      },
    }))
  })

  it('test 1 Owned - Affichage d\'un élément dans la liste des publications', async () => {
    const item1 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU PREMIER TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    ItemService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => 'link',
        },
      }),
    )
    ClassificationService.highlighted = vi.fn().mockReturnValue(
      Promise.resolve({
        data: { name: 'À la une' },
      }),
    )
    EnumDatasService.getItemStatusList = vi.fn().mockReturnValue([
      { id: 0, name: 'PENDING', label: 'enum.itemStatus.pending.title' },
      { id: 4, name: 'DRAFT', label: 'enum.itemStatus.draft.title' },
    ])
    ParseLinkUtils.parse = vi.fn().mockReturnValue({ last: 1, first: 1 })
    DateUtils.convertToIntString = vi.fn().mockReturnValue('1 janv. 2022, 12:00:00')

    const $t = param => param
    const $route = { params: { itemState: '' } }
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    }
    const organizations = [{ name: 'College' }, { name: 'Lycée' }]

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.vm.items.length).toStrictEqual(1)
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(1)
    expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1)
    expect(ItemService.query).toHaveBeenCalledTimes(1)
    expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1)
  })

  it('test 2 Owned - Affichage de deux éléments dans la liste des publications', async () => {
    const item1 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU PREMIER TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    const item2 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE 2',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU SECOND TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    ItemService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1, item2]),
        headers: {
          get: () => 'link',
        },
      }),
    )
    ClassificationService.highlighted = vi.fn().mockReturnValue(
      Promise.resolve({
        data: { name: 'À la une' },
      }),
    )
    EnumDatasService.getItemStatusList = vi.fn().mockReturnValue([
      { id: 0, name: 'PENDING', label: 'enum.itemStatus.pending.title' },
      { id: 4, name: 'DRAFT', label: 'enum.itemStatus.draft.title' },
    ])
    ParseLinkUtils.parse = vi.fn().mockReturnValue({ last: 1, first: 1 })
    DateUtils.convertToIntString = vi.fn().mockReturnValue('1 janv. 2022, 12:00:00')

    const $t = param => param
    const $route = { params: { itemState: '' } }
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    }
    const organizations = [{ name: 'College' }, { name: 'Lycée' }]

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.vm.items.length).toStrictEqual(2)
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(2)
    expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1)
    expect(ItemService.query).toHaveBeenCalledTimes(1)
    expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1)
  })

  it('test 3 Owned - itemDetail', async () => {
    const item1 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU PREMIER TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    ItemService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => 'link',
        },
      }),
    )
    ClassificationService.highlighted = vi.fn().mockReturnValue(
      Promise.resolve({
        data: { name: 'À la une' },
      }),
    )
    EnumDatasService.getItemStatusList = vi.fn().mockReturnValue([
      { id: 0, name: 'PENDING', label: 'enum.itemStatus.pending.title' },
      { id: 4, name: 'DRAFT', label: 'enum.itemStatus.draft.title' },
    ])
    ParseLinkUtils.parse = vi.fn().mockReturnValue({ last: 1, first: 1 })
    DateUtils.convertToIntString = vi.fn().mockReturnValue('1 janv. 2022, 12:00:00')

    const $t = param => param
    const $route = { params: { itemState: '', fullPath: 'fullPath' } }
    const $router = {
      push: vi.fn(),
    }
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    }
    const organizations = [{ name: 'College' }, { name: 'Lycée' }]

    const wrapper = shallowMount(Owned, {
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
        provide: {
          organizations() {
            return organizations
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.vm.items.length).toStrictEqual(1)
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(1)
    await wrapper.vm.itemDetail(item1)
    expect($router.push).toHaveBeenCalledTimes(1)
    expect($router.push).toHaveBeenCalledWith({
      name: 'ContentDetail',
      params: { id: item1.id },
    })
    expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1)
    expect(ItemService.query).toHaveBeenCalledTimes(1)
    expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1)
  })

  it('test 4 Owned - update', async () => {
    const item1 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU PREMIER TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    ItemService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => 'link',
        },
      }),
    )
    ClassificationService.highlighted = vi.fn().mockReturnValue(
      Promise.resolve({
        data: { name: 'À la une' },
      }),
    )
    EnumDatasService.getItemStatusList = vi.fn().mockReturnValue([
      { id: 0, name: 'PENDING', label: 'enum.itemStatus.pending.title' },
      { id: 4, name: 'DRAFT', label: 'enum.itemStatus.draft.title' },
    ])
    ParseLinkUtils.parse = vi.fn().mockReturnValue({ last: 1, first: 1 })
    DateUtils.convertToIntString = vi.fn().mockReturnValue('1 janv. 2022, 12:00:00')

    const $t = param => param
    const $route = { params: { itemState: '', fullPath: 'fullPath' } }
    const $router = {
      push: vi.fn(),
    }
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    }
    const organizations = [{ name: 'College' }, { name: 'Lycée' }]

    const wrapper = shallowMount(Owned, {
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
        provide: {
          organizations() {
            return organizations
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.vm.items.length).toStrictEqual(1)
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(1)
    await wrapper.vm.update(item1.id)
    expect($router.push).toHaveBeenCalledTimes(1)
    expect($router.push).toHaveBeenCalledWith({
      name: 'PublishPublisher',
      params: { id: item1.id },
    })
    expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1)
    expect(ItemService.query).toHaveBeenCalledTimes(1)
    expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1)
  })

  it('test 5 Owned - deleteItem', async () => {
    const item1 = {
      type: 'ATTACHMENT',
      id: 39,
      createdBy: { displayName: 'Administrator' },
      createdDate: '2022-01-28T16:04:50Z',
      lastModifiedBy: { displayName: 'Administrator' },
      lastModifiedDate: '2022-01-28T16:04:50Z',
      title: 'PREMIER TEST VIABLE',
      enclosure: null,
      endDate: '2022-01-29',
      startDate: '2022-01-28',
      validatedBy: { displayName: 'Administrator' },
      validatedDate: '2022-01-29',
      status: 'DRAFT',
      summary: 'DESCRIPTION DU PREMIER TEST VIABLE',
      rssAllowed: false,
      highlight: false,
      organization: { name: 'Collège De L\'Iroise' },
      redactor: { displayName: 'Administrator' },
    }
    ItemService.query = vi.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => 'link',
        },
      }),
    )
    ClassificationService.highlighted = vi.fn().mockReturnValue(
      Promise.resolve({
        data: { name: 'À la une' },
      }),
    )
    EnumDatasService.getItemStatusList = vi.fn().mockReturnValue([
      { id: 0, name: 'PENDING', label: 'enum.itemStatus.pending.title' },
      { id: 4, name: 'DRAFT', label: 'enum.itemStatus.draft.title' },
    ])
    ParseLinkUtils.parse = vi.fn().mockReturnValue({ last: 1, first: 1 })
    DateUtils.convertToIntString = vi.fn().mockReturnValue('1 janv. 2022, 12:00:00')

    ItemService.get = vi.fn().mockReturnValue(Promise.resolve({ data: item1 }))
    ContentService.delete = vi.fn().mockReturnValue(Promise.resolve({}))
    const $t = param => param
    const $route = { params: { itemState: '', fullPath: 'fullPath' } }
    const $store = {
      getters: {
        getLanguage: 'fr',
      },
    }
    const organizations = [{ name: 'College' }, { name: 'Lycée' }]

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.vm.items.length).toStrictEqual(1)
    expect(wrapper.element.querySelectorAll('tbody>tr').length).toStrictEqual(1)
    await wrapper.vm.deleteItem(item1.id)
    expect(ItemService.get).toHaveBeenCalledTimes(1)
    await wrapper.vm.confirmDelete(item1.id)
    expect(ContentService.delete).toHaveBeenCalledTimes(1)
    expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1)
    expect(ItemService.query).toHaveBeenCalledTimes(2)
    expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1)
  })
})
