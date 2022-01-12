jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICategoryVitamins, CategoryVitamins } from '../category-vitamins.model';
import { CategoryVitaminsService } from '../service/category-vitamins.service';

import { CategoryVitaminsRoutingResolveService } from './category-vitamins-routing-resolve.service';

describe('CategoryVitamins routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CategoryVitaminsRoutingResolveService;
  let service: CategoryVitaminsService;
  let resultCategoryVitamins: ICategoryVitamins | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CategoryVitaminsRoutingResolveService);
    service = TestBed.inject(CategoryVitaminsService);
    resultCategoryVitamins = undefined;
  });

  describe('resolve', () => {
    it('should return ICategoryVitamins returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryVitamins = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryVitamins).toEqual({ id: 123 });
    });

    it('should return new ICategoryVitamins if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryVitamins = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCategoryVitamins).toEqual(new CategoryVitamins());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CategoryVitamins })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryVitamins = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryVitamins).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
