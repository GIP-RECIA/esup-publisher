import { beforeEach, describe, expect, it, vi } from 'vitest'
import LogsService from '@/services/admin/LogsService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

vi.mock('@/services/util/FetchWrapper.js')

describe('logsService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 LogsService - findAll', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    LogsService.findAll().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/logs')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 2 LogsService - changeLevel', () => {
    const response = {
      data: {},
      headers: [],
    }
    FetchWrapper.putJson = vi.fn().mockReturnValue(Promise.resolve(response))

    LogsService.changeLevel('myName', 'myLevel').then((value) => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/logs', {
        name: 'myName',
        level: 'myLevel',
      })
      expect(value).toStrictEqual(response)
    })
  })
})
