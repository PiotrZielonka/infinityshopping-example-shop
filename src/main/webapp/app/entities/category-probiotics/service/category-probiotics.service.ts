import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategoryProbiotics } from '../category-probiotics.model';

export type EntityResponseType = HttpResponse<ICategoryProbiotics>;
export type EntityArrayResponseType = HttpResponse<ICategoryProbiotics[]>;

@Injectable({ providedIn: 'root' })
export class CategoryProbioticsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/category-probiotics');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}
 
  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategoryProbiotics>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategoryProbiotics[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
 
  protected convertDateFromClient(categoryProbiotics: ICategoryProbiotics): ICategoryProbiotics {
    return Object.assign({}, categoryProbiotics, {
      createTime: categoryProbiotics.createTime?.isValid() ? categoryProbiotics.createTime.toJSON() : undefined,
      updateTime: categoryProbiotics.updateTime?.isValid() ? categoryProbiotics.updateTime.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createTime = res.body.createTime ? dayjs(res.body.createTime) : undefined;
      res.body.updateTime = res.body.updateTime ? dayjs(res.body.updateTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((categoryProbiotics: ICategoryProbiotics) => {
        categoryProbiotics.createTime = categoryProbiotics.createTime ? dayjs(categoryProbiotics.createTime) : undefined;
        categoryProbiotics.updateTime = categoryProbiotics.updateTime ? dayjs(categoryProbiotics.updateTime) : undefined;
      });
    }
    return res;
  }
}
