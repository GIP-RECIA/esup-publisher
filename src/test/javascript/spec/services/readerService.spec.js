import { beforeEach, describe, expect, it, vi } from 'vitest'
import ReaderService from '@/services/entities/reader/ReaderService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

vi.mock('@/services/util/FetchWrapper.js')

describe('readerService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 ReaderService - query', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    ReaderService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/readers')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 2 ReaderService - get', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    ReaderService.get(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/readers/${id}`)
      expect(value).toStrictEqual(response)
    })
  })

  it('test 3 ReaderService - update', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.putJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const data = {
      id: 1,
    }
    ReaderService.update(data).then((value) => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/readers', data)
      expect(value).toStrictEqual(response)
    })
  })

  it('test 4 ReaderService - delete', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.deleteJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    ReaderService.delete(id).then((value) => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith(`api/readers/${id}`)
      expect(value).toStrictEqual(response)
    })
  })
})
