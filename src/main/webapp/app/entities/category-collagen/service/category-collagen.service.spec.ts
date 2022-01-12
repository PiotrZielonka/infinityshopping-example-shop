import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ProductCategoryEnum } from 'app/entities/enumerations/product-category-enum.model';
import { ICategoryCollagen } from '../category-collagen.model';

import { CategoryCollagenService } from './category-collagen.service';

describe('CategoryCollagen Service', () => {
  let service: CategoryCollagenService;
  let httpMock: HttpTestingController;
  let elemDefault: ICategoryCollagen;
  let expectedResult: ICategoryCollagen | ICategoryCollagen[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CategoryCollagenService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      productCategoryEnum: ProductCategoryEnum.Aloes,
      name: 'AAAAAAA',
      quantity: 0,
      priceNet: 0,
      vat: 0,
      priceGross: 0,
      stock: 0,
      description: 'AAAAAAA',
      createTime: currentDate,
      updateTime: currentDate,
      imageContentType: 'image/png',
      image: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createTime: currentDate.format(DATE_TIME_FORMAT),
          updateTime: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });
    
    it('should return a list of CategoryCollagen', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          productCategoryEnum: 'BBBBBB',
          name: 'BBBBBB',
          quantity: 1,
          priceNet: 1,
          vat: 1,
          priceGross: 1,
          stock: 1,
          description: 'BBBBBB',
          createTime: currentDate.format(DATE_TIME_FORMAT),
          updateTime: currentDate.format(DATE_TIME_FORMAT),
          image: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createTime: currentDate,
          updateTime: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
