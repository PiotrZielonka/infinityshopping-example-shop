jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICategoryProbiotics, CategoryProbiotics } from '../category-probiotics.model';
import { CategoryProbioticsService } from '../service/category-probiotics.service';

import { CategoryProbioticsRoutingResolveService } from './category-probiotics-routing-resolve.service';

describe('CategoryProbiotics routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CategoryProbioticsRoutingResolveService;
  let service: CategoryProbioticsService;
  let resultCategoryProbiotics: ICategoryProbiotics | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CategoryProbioticsRoutingResolveService);
    service = TestBed.inject(CategoryProbioticsService);
    resultCategoryProbiotics = undefined;
  });

  describe('resolve', () => {
    it('should return ICategoryProbiotics returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryProbiotics = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryProbiotics).toEqual({ id: 123 });
    });

    it('should return new ICategoryProbiotics if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryProbiotics = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCategoryProbiotics).toEqual(new CategoryProbiotics());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CategoryProbiotics })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryProbiotics = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryProbiotics).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
