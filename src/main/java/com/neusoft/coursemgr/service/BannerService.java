package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.Banner;

import java.util.List;

public interface BannerService {

    List<Banner> listEnabled();

    List<Banner> adminListAll(Long adminUserId);

    Long adminCreate(Long adminUserId, Banner banner);

    void adminUpdate(Long adminUserId, Banner banner);

    void adminUpdateStatus(Long adminUserId, Long id, Integer status);

    void adminDelete(Long adminUserId, Long id);
}
