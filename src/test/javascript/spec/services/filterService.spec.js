import { beforeEach, describe, expect, it, vi } from 'vitest'
import FilterService from '@/services/entities/filter/FilterService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

vi.mock('@/services/util/FetchWrapper.js')

describe('filterService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 FilterService - query', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    FilterService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/filters')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 2 FilterService - get', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    FilterService.get(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/filters/${id}`)
      expect(value).toStrictEqual(response)
    })
  })

  it('test 3 FilterService - update', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.putJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const data = {
      id: 1,
    }
    FilterService.update(data).then((value) => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/filters', data)
      expect(value).toStrictEqual(response)
    })
  })

  it('test 4 FilterService - delete', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.deleteJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    FilterService.delete(id).then((value) => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith(`api/filters/${id}`)
      expect(value).toStrictEqual(response)
    })
  })
})
