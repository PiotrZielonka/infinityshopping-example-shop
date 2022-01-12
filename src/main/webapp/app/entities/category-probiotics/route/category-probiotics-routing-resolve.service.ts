import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategoryProbiotics, CategoryProbiotics } from '../category-probiotics.model';
import { CategoryProbioticsService } from '../service/category-probiotics.service';

@Injectable({ providedIn: 'root' })
export class CategoryProbioticsRoutingResolveService implements Resolve<ICategoryProbiotics> {
  constructor(protected service: CategoryProbioticsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategoryProbiotics> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categoryProbiotics: HttpResponse<CategoryProbiotics>) => {
          if (categoryProbiotics.body) {
            return of(categoryProbiotics.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CategoryProbiotics());
  }
}
