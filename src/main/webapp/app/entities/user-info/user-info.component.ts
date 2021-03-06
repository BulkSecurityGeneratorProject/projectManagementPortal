import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiParseLinks, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { UserInfo } from './user-info.model';
import { UserInfoService } from './user-info.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { ActivatedRoute, Router } from '@angular/router';
import { VIEW_TEAMS_My, VIEW_TEAMS_ALL, VIEW_TEAMS_IM_MEMBER_OF,
    VIEW_TEAMS_BROWSE_MORE, VIEW_TEAMS_USERS_HEADED,
    VIEW_TEAMS_USERS_MEMBER_OF, VIEW_TEAMS_USERS_MEMBER_OF_MY,
    ACTION_JOIN_TEAM, ACTION_ADD_USERS_TO_TEAM, ACTION_TEAMS_TO_USER } from './../../shared/constants/page.constants';

@Component({
    selector: 'jhi-user-info',
    templateUrl: './user-info.component.html',
    styleUrls: [
        'user-info.css'
    ]
})
export class UserInfoComponent implements OnInit, OnDestroy {

    userInfos: UserInfo[];
    currentAccount: any;
    error: any;
    success: any;
    eventSubscriber: Subscription;
    routeData: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any;
    links: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;
    isTabular: boolean;

    usersHeadedTeams = VIEW_TEAMS_USERS_HEADED;
    usersMemberOf = VIEW_TEAMS_USERS_MEMBER_OF;
    usersMemberOfMyTeams = VIEW_TEAMS_USERS_MEMBER_OF_MY;
    action: string;
    joinTeam = ACTION_JOIN_TEAM;
    addUsersToTeam = ACTION_ADD_USERS_TO_TEAM;
    addTeamsToUser = ACTION_TEAMS_TO_USER;

    constructor(
        private userInfoService: UserInfoService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private principal: Principal,
        private activatedRoute: ActivatedRoute,
        private dataUtils: JhiDataUtils,
        private router: Router,
    ) {
        // this.userInfos = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
    }

    loadAll() {
        this.userInfoService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }
    transition() {
        this.router.navigate(['/user-info'], {queryParams:
            {
                page: this.page,
                size: this.itemsPerPage,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.loadAll();
    }

    clear() {
        this.page = 0;
        this.router.navigate(['/user-info', {
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInUserInfos();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: UserInfo) {
        return item.id;
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    registerChangeInUserInfos() {
        this.eventSubscriber = this.eventManager.subscribe('userInfoListModification', (response) => this.loadAll());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onSuccess(data, headers) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.userInfos = data;
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
