jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICategoryMinerals, CategoryMinerals } from '../category-minerals.model';
import { CategoryMineralsService } from '../service/category-minerals.service';

import { CategoryMineralsRoutingResolveService } from './category-minerals-routing-resolve.service';

describe('CategoryMinerals routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CategoryMineralsRoutingResolveService;
  let service: CategoryMineralsService;
  let resultCategoryMinerals: ICategoryMinerals | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CategoryMineralsRoutingResolveService);
    service = TestBed.inject(CategoryMineralsService);
    resultCategoryMinerals = undefined;
  });

  describe('resolve', () => {
    it('should return ICategoryMinerals returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryMinerals = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryMinerals).toEqual({ id: 123 });
    });

    it('should return new ICategoryMinerals if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryMinerals = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCategoryMinerals).toEqual(new CategoryMinerals());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CategoryMinerals })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryMinerals = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryMinerals).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
