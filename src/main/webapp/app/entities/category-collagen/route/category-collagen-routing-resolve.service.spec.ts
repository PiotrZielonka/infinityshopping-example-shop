jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICategoryCollagen, CategoryCollagen } from '../category-collagen.model';
import { CategoryCollagenService } from '../service/category-collagen.service';

import { CategoryCollagenRoutingResolveService } from './category-collagen-routing-resolve.service';

describe('CategoryCollagen routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CategoryCollagenRoutingResolveService;
  let service: CategoryCollagenService;
  let resultCategoryCollagen: ICategoryCollagen | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CategoryCollagenRoutingResolveService);
    service = TestBed.inject(CategoryCollagenService);
    resultCategoryCollagen = undefined;
  });

  describe('resolve', () => {
    it('should return ICategoryCollagen returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryCollagen = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryCollagen).toEqual({ id: 123 });
    });

    it('should return new ICategoryCollagen if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryCollagen = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCategoryCollagen).toEqual(new CategoryCollagen());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CategoryCollagen })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryCollagen = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryCollagen).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
