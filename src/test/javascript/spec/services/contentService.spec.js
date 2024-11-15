import ContentService from '@/services/entities/content/ContentService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'
import { beforeEach, describe, expect, it, vi } from 'vitest'

vi.mock('@/services/util/FetchWrapper.js')

describe('contentService.js tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('test 1 ContentService - query', () => {
    const response = {
      data: [],
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    ContentService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/contents')
      expect(value).toStrictEqual(response)
    })
  })

  it('test 2 ContentService - get', () => {
    const response = {
      data: {
        item: {
          endDate: '2021-12-01',
          startDate: '2021-12-02',
          validatedDate: '2021-12-03',
          createdDate: '2021-12-04',
          lastModifiedDate: '2021-12-05',
          body: '<a href="link/to/file">test</a>',
        },
        linkedFiles: [
          {
            uri: 'link/to/file',
          },
          {
            uri: 'link/to/file2',
          },
        ],
      },
      headers: [],
    }
    FetchWrapper.getJson = vi.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    ContentService.get(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(`api/contents/${id}`)
      expect(toString.call(value.data.item.endDate)).toStrictEqual('[object Date]')
      expect(toString.call(value.data.item.startDate)).toStrictEqual('[object Date]')
      expect(toString.call(value.data.item.validatedDate)).toStrictEqual('[object Date]')
      expect(toString.call(value.data.item.createdDate)).toStrictEqual('[object Date]')
      expect(toString.call(value.data.item.lastModifiedDate)).toStrictEqual('[object Date]')
      expect(value.data.item.body).toStrictEqual(`<a href="${process.env.VITE_BACK_BASE_URL}link/to/file">test</a>`)
    })
  })

  it('test 3 ContentService - update', () => {
    FetchWrapper.putJson = vi.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      item: {
        endDate: new Date('2021', '11', '01'),
        startDate: new Date('2021', '11', '02'),
        body: `<a href="${process.env.VITE_BACK_BASE_URL}link/to/file">test</a>`,
      },
      linkedFiles: [
        {
          uri: 'link/to/file',
        },
        {
          uri: 'link/to/file2',
        },
      ],
    }
    ContentService.update(data).then(() => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith(
        'api/contents',
        expect.objectContaining({
          item: expect.objectContaining({
            endDate: '2021-12-01',
            startDate: '2021-12-02',
            body: '<a href="link/to/file">test</a>',
          }),
        }),
      )
    })
  })

  it('test 4 ContentService - save', () => {
    FetchWrapper.postJson = vi.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      item: {
        endDate: new Date('2021', '11', '01'),
        startDate: new Date('2021', '11', '02'),
        body: `<a href="${process.env.VITE_BACK_BASE_URL}link/to/file">test</a>`,
      },
      linkedFiles: [
        {
          uri: 'link/to/file',
        },
        {
          uri: 'link/to/file2',
        },
      ],
    }
    ContentService.save(data).then(() => {
      expect(FetchWrapper.postJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.postJson).toHaveBeenCalledWith(
        'api/contents',
        expect.objectContaining({
          item: expect.objectContaining({
            endDate: '2021-12-01',
            startDate: '2021-12-02',
            body: '<a href="link/to/file">test</a>',
          }),
        }),
      )
    })
  })
})
