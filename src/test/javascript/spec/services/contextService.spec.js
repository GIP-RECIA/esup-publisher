import { beforeEach, describe, expect, it, vi } from 'vitest'
import ContextService from '@/services/entities/context/ContextService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

vi.mock('@/services/util/FetchWrapper.js')

describe('contextService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 ContextService - query', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const search = 1
    ContextService.query(search).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/contexts?search=${search}`)
      expect(value).toStrictEqual(response)
    })
  })
})
