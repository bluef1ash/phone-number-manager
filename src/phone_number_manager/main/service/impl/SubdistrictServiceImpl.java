package main.service.impl;

import org.springframework.stereotype.Service;

import main.entity.Subdistrict;
import main.service.SubdistrictService;
/**
 * 街道业务实现
 *
 */
@Service("subdistrictService")
public class SubdistrictServiceImpl extends BaseServiceImpl<Subdistrict> implements SubdistrictService {}