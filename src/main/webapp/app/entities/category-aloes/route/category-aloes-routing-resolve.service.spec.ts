jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICategoryAloes, CategoryAloes } from '../category-aloes.model';
import { CategoryAloesService } from '../service/category-aloes.service';

import { CategoryAloesRoutingResolveService } from './category-aloes-routing-resolve.service';

describe('CategoryAloes routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CategoryAloesRoutingResolveService;
  let service: CategoryAloesService;
  let resultCategoryAloes: ICategoryAloes | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CategoryAloesRoutingResolveService);
    service = TestBed.inject(CategoryAloesService);
    resultCategoryAloes = undefined;
  });

  describe('resolve', () => {
    it('should return ICategoryAloes returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryAloes = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryAloes).toEqual({ id: 123 });
    });

    it('should return new ICategoryAloes if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryAloes = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCategoryAloes).toEqual(new CategoryAloes());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CategoryAloes })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCategoryAloes = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCategoryAloes).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
