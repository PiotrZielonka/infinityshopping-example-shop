import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategoryCollagen, CategoryCollagen } from '../category-collagen.model';
import { CategoryCollagenService } from '../service/category-collagen.service';

@Injectable({ providedIn: 'root' })
export class CategoryCollagenRoutingResolveService implements Resolve<ICategoryCollagen> {
  constructor(protected service: CategoryCollagenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategoryCollagen> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categoryCollagen: HttpResponse<CategoryCollagen>) => {
          if (categoryCollagen.body) {
            return of(categoryCollagen.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CategoryCollagen());
  }
}
