import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategoryCollagen } from '../category-collagen.model';

export type EntityResponseType = HttpResponse<ICategoryCollagen>;
export type EntityArrayResponseType = HttpResponse<ICategoryCollagen[]>;


@Injectable({ providedIn: 'root' })
export class CategoryCollagenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/category-collagen');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  
  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategoryCollagen>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategoryCollagen[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
  
  protected convertDateFromClient(categoryCollagen: ICategoryCollagen): ICategoryCollagen {
    return Object.assign({}, categoryCollagen, {
      createTime: categoryCollagen.createTime?.isValid() ? categoryCollagen.createTime.toJSON() : undefined,
      updateTime: categoryCollagen.updateTime?.isValid() ? categoryCollagen.updateTime.toJSON() : undefined,
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
      res.body.forEach((categoryCollagen: ICategoryCollagen) => {
        categoryCollagen.createTime = categoryCollagen.createTime ? dayjs(categoryCollagen.createTime) : undefined;
        categoryCollagen.updateTime = categoryCollagen.updateTime ? dayjs(categoryCollagen.updateTime) : undefined;
      });
    }
    return res;
  }
}
