import { beforeEach, describe, expect, it, vi } from 'vitest'
import PublisherService from '@/services/entities/publisher/PublisherService.js'
import DateUtils from '@/services/util/DateUtils'
import FetchWrapper from '@/services/util/FetchWrapper.js'

vi.mock('@/services/util/FetchWrapper.js')

describe('publisherService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 PublisherService - query', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    PublisherService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 2 PublisherService - query with params', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    PublisherService.query({ key1: 'val1', key2: 'val2' }).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers?key1=val1&key2=val2')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 3 PublisherService - get', () => {
    const response = {
      data: {
        id: 1,
        createdDate: '2022-01-20T10:30:44Z',
        lastModifiedDate: '2022-04-12T11:58:02Z',
      },
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    PublisherService.get(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/publishers/${id}`)
      expect(value).toStrictEqual({
        data: {
          id: response.data.id,
          createdDate: DateUtils.convertDateTimeFromServer(response.data.createdDate),
          lastModifiedDate: DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate),
        },
        headers: [],
      })
    })
  })

  it('test 4 PublisherService - update', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.putJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const data = {
      id: 1,
    }
    PublisherService.update(data).then((value) => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/publishers', data)
      expect(value).toStrictEqual(response)
    })
  })

  it('test 5 PublisherService - delete', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.deleteJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    PublisherService.delete(id).then((value) => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith(`api/publishers/${id}`)
      expect(value).toStrictEqual(response)
    })
  })
})
