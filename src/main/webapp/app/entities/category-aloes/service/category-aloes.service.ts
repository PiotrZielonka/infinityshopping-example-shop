import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategoryAloes } from '../category-aloes.model';

export type EntityResponseType = HttpResponse<ICategoryAloes>;
export type EntityArrayResponseType = HttpResponse<ICategoryAloes[]>;


@Injectable({ providedIn: 'root' })
export class CategoryAloesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/category-aloes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategoryAloes>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategoryAloes[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(categoryAloes: ICategoryAloes): ICategoryAloes {
    return Object.assign({}, categoryAloes, {
      createTime: categoryAloes.createTime?.isValid() ? categoryAloes.createTime.toJSON() : undefined,
      updateTime: categoryAloes.updateTime?.isValid() ? categoryAloes.updateTime.toJSON() : undefined,
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
      res.body.forEach((categoryAloes: ICategoryAloes) => {
        categoryAloes.createTime = categoryAloes.createTime ? dayjs(categoryAloes.createTime) : undefined;
        categoryAloes.updateTime = categoryAloes.updateTime ? dayjs(categoryAloes.updateTime) : undefined;
      });
    }
    return res;
  }
}
