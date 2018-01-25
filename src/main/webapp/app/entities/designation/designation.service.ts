import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { Designation } from './designation.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class DesignationService {

    private resourceUrl =  SERVER_API_URL + 'api/designations';

    constructor(private http: Http) { }

    create(designation: Designation): Observable<Designation> {
        const copy = this.convert(designation);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    update(designation: Designation): Observable<Designation> {
        const copy = this.convert(designation);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    find(id: number): Observable<Designation> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        const result = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            result.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return new ResponseWrapper(res.headers, result, res.status);
    }

    /**
     * Convert a returned JSON object to Designation.
     */
    private convertItemFromServer(json: any): Designation {
        const entity: Designation = Object.assign(new Designation(), json);
        return entity;
    }

    /**
     * Convert a Designation to a JSON which can be sent to the server.
     */
    private convert(designation: Designation): Designation {
        const copy: Designation = Object.assign({}, designation);
        return copy;
    }
}
