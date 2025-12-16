package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.Banner;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.BannerMapper;
import com.neusoft.coursemgr.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    private final BannerMapper bannerMapper;

    public BannerServiceImpl(BannerMapper bannerMapper) {
        this.bannerMapper = bannerMapper;
    }

    @Override
    public List<Banner> listEnabled() {
        return bannerMapper.selectEnabled();
    }

    @Override
    public List<Banner> adminListAll(Long adminUserId) {
        return bannerMapper.selectAll();
    }

    @Override
    public Long adminCreate(Long adminUserId, Banner banner) {
        banner.setStatus(1);
        bannerMapper.insert(banner);
        log.info("admin create banner, adminUserId={}, bannerId={}", adminUserId, banner.getId());
        return banner.getId();
    }

    @Override
    public void adminUpdate(Long adminUserId, Banner banner) {
        Banner exist = bannerMapper.selectById(banner.getId());
        if (exist == null) {
            throw new BizException(404, "轮播图不存在");
        }
        bannerMapper.update(banner);
        log.info("admin update banner, adminUserId={}, bannerId={}", adminUserId, banner.getId());
    }

    @Override
    public void adminUpdateStatus(Long adminUserId, Long id, Integer status) {
        int rows = bannerMapper.updateStatus(id, status);
        if (rows <= 0) {
            throw new BizException(404, "轮播图不存在");
        }
        log.info("admin update banner status, adminUserId={}, bannerId={}, status={}", adminUserId, id, status);
    }

    @Override
    public void adminDelete(Long adminUserId, Long id) {
        int rows = bannerMapper.deleteById(id);
        if (rows <= 0) {
            throw new BizException(404, "轮播图不存在");
        }
        log.info("admin delete banner, adminUserId={}, bannerId={}", adminUserId, id);
    }
}
