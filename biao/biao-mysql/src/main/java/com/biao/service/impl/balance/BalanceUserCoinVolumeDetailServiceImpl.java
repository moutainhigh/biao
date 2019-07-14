package com.biao.service.impl.balance;

import com.biao.entity.Coin;
import com.biao.entity.PlatUser;
import com.biao.entity.balance.*;
import com.biao.mapper.CoinDao;
import com.biao.mapper.PlatUserDao;
import com.biao.mapper.balance.*;
import com.biao.redis.RedisCacheManager;
import com.biao.service.UserCoinVolumeExService;
import com.biao.service.balance.BalanceUserCoinVolumeDetailService;
import com.biao.service.balance.BalanceUserCoinVolumeService;
import com.biao.util.DateUtils;
import com.biao.util.SnowFlake;
import com.biao.vo.KlineVO;
import com.biao.vo.TradePairVO;
import com.biao.vo.redis.RedisExPairVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *余币宝统计和明细service
 */
@Service
public class BalanceUserCoinVolumeDetailServiceImpl implements BalanceUserCoinVolumeDetailService {

    @Autowired(required = false)
    private BalanceUserCoinVolumeDetailDao balanceUserCoinVolumeDetailDao;

    @Autowired(required = false)
    private BalanceUserVolumeIncomeDetailDao balanceUserVolumeIncomeDetailDao;

    @Autowired(required = false)
    private BalanceUserCoinVolumeDao balanceUserCoinVolumeDao;

    @Autowired
    private UserCoinVolumeExService userCoinVolumeExService;

    @Autowired(required = false)
    private PlatUserDao platUserDao;

    @Autowired
    private CoinDao coinDao;

    @Autowired(required = false)
    private BalanceChangeUserCoinVolumeDao balanceChangeUserCoinVolumeDao;

    @Autowired(required = false)
    private BalanceUserCoinCountVolumeDao balanceUserCoinCountVolumeDao;

    @Autowired(required = false)
    private BalancePlatJackpotVolumeDetailDao balancePlatJackpotVolumeDetailDao;

    @Override
    public String save(BalanceUserCoinVolumeDetail balanceUserCoinVolumeDetail) {
        String id = SnowFlake.createSnowFlake().nextIdString();

        balanceUserCoinVolumeDetail.setId(id);
        balanceUserCoinVolumeDetailDao.insert(balanceUserCoinVolumeDetail);
        return id;
    }
    @Override
    public void updateById(BalanceUserCoinVolumeDetail balanceUserCoinVolumeDetail) {
        balanceUserCoinVolumeDetailDao.updateById(balanceUserCoinVolumeDetail);
    }

    @Override
    public List<BalanceUserCoinVolumeDetail> findAll(String userId) {
        return balanceUserCoinVolumeDetailDao.findByUserId(userId);
    }

    @Override
    public void  balanceIncomeCount(){
        List<BalanceUserCoinCountVolume>  balanceUserCoinVolumeList= balanceUserCoinCountVolumeDao.findAll();
        if (CollectionUtils.isNotEmpty(balanceUserCoinVolumeList)) {
            balanceUserCoinVolumeList.forEach(e -> {
                List<BalanceUserCoinVolumeDetail> balanceDetailList=balanceUserCoinVolumeDetailDao.findByUserIdAndCoin(e.getUserId(),e.getCoinSymbol());
                if (CollectionUtils.isNotEmpty(balanceDetailList)) {
                    balanceDetailList.forEach(balanceDetail -> {
                       if(balanceDetail.getStaticsIncome() != null && balanceDetail.getStaticsIncome().compareTo(BigDecimal.ZERO)>0) {
                           BalanceUserVolumeIncomeDetail balanceIncomeDetail = new BalanceUserVolumeIncomeDetail();
                           String id = SnowFlake.createSnowFlake().nextIdString();
                           balanceIncomeDetail.setId(id);
                           balanceIncomeDetail.setRewardType("1");
                           balanceIncomeDetail.setDetailReward(balanceDetail.getStaticsIncome());
                           balanceIncomeDetail.setCoinSymbol(e.getCoinPlatSymbol());
                           balanceIncomeDetail.setIncomeDate(balanceDetail.getIncomeDate());
                           balanceIncomeDetail.setUserId(e.getUserId());
                           balanceIncomeDetail.setVersion(0);
                           balanceIncomeDetail.setCreateDate(LocalDateTime.now());
                           balanceUserVolumeIncomeDetailDao.insert(balanceIncomeDetail);
                       }
                        if(balanceDetail.getDynamicsIncome() != null && balanceDetail.getDynamicsIncome().compareTo(BigDecimal.ZERO)>0) {
                            BalanceUserVolumeIncomeDetail balanceIncomeDetail2=new BalanceUserVolumeIncomeDetail();
                            String id2 = SnowFlake.createSnowFlake().nextIdString();
                            balanceIncomeDetail2.setId(id2);
                            balanceIncomeDetail2.setRewardType("2");
                            balanceIncomeDetail2.setDetailReward(balanceDetail.getDynamicsIncome());
                            balanceIncomeDetail2.setCoinSymbol(e.getCoinPlatSymbol());
                            balanceIncomeDetail2.setIncomeDate(balanceDetail.getIncomeDate());
                            balanceIncomeDetail2.setUserId(e.getUserId());
                            balanceIncomeDetail2.setVersion(0);
                            balanceIncomeDetail2.setCreateDate(LocalDateTime.now());
                            balanceUserVolumeIncomeDetailDao.insert(balanceIncomeDetail2);
                        }
                        if(balanceDetail.getCommunityManageReward() != null && balanceDetail.getCommunityManageReward().compareTo(BigDecimal.ZERO)>0) {
                            BalanceUserVolumeIncomeDetail balanceIncomeDetail3=new BalanceUserVolumeIncomeDetail();
                            String id3 = SnowFlake.createSnowFlake().nextIdString();
                            balanceIncomeDetail3.setId(id3);
                            balanceIncomeDetail3.setRewardType("3");
                            balanceIncomeDetail3.setDetailReward(balanceDetail.getCommunityManageReward());
                            balanceIncomeDetail3.setCoinSymbol(e.getCoinPlatSymbol());
                            balanceIncomeDetail3.setIncomeDate(balanceDetail.getIncomeDate());
                            balanceIncomeDetail3.setUserId(e.getUserId());
                            balanceIncomeDetail3.setVersion(0);
                            balanceIncomeDetail3.setCreateDate(LocalDateTime.now());
                            balanceUserVolumeIncomeDetailDao.insert(balanceIncomeDetail3);
                        }
                        if(balanceDetail.getEqualityReward() != null && balanceDetail.getEqualityReward().compareTo(BigDecimal.ZERO)>0) {
                            BalanceUserVolumeIncomeDetail balanceIncomeDetail4=new BalanceUserVolumeIncomeDetail();
                            String id4 = SnowFlake.createSnowFlake().nextIdString();
                            balanceIncomeDetail4.setId(id4);
                            balanceIncomeDetail4.setRewardType("4");
                            balanceIncomeDetail4.setDetailReward(balanceDetail.getEqualityReward());
                            balanceIncomeDetail4.setCoinSymbol(e.getCoinPlatSymbol());
                            balanceIncomeDetail4.setIncomeDate(balanceDetail.getIncomeDate());
                            balanceIncomeDetail4.setUserId(e.getUserId());
                            balanceIncomeDetail4.setVersion(0);
                            balanceIncomeDetail4.setCreateDate(LocalDateTime.now());
                            balanceUserVolumeIncomeDetailDao.insert(balanceIncomeDetail4);
                        }
                        if(balanceDetail.getLevelDifferenceReward() != null && balanceDetail.getLevelDifferenceReward().compareTo(BigDecimal.ZERO)>0) {
                            BalanceUserVolumeIncomeDetail balanceIncomeDetail5=new BalanceUserVolumeIncomeDetail();
                            String id5 = SnowFlake.createSnowFlake().nextIdString();
                            balanceIncomeDetail5.setId(id5);
                            balanceIncomeDetail5.setRewardType("5");
                            balanceIncomeDetail5.setDetailReward(balanceDetail.getLevelDifferenceReward());
                            balanceIncomeDetail5.setCoinSymbol(e.getCoinPlatSymbol());
                            balanceIncomeDetail5.setIncomeDate(balanceDetail.getIncomeDate());
                            balanceIncomeDetail5.setUserId(e.getUserId());
                            balanceIncomeDetail5.setVersion(0);
                            balanceIncomeDetail5.setCreateDate(LocalDateTime.now());
                            balanceUserVolumeIncomeDetailDao.insert(balanceIncomeDetail5);
                        }

                        //总资产计算
                        e.setCoinBalance(e.getCoinBalance());
                        e.setYesterdayIncome(balanceDetail.getDetailIncome().add(balanceDetail.getDetailReward()));
                        if(e.getAccumulIncome() !=null){
                            e.setAccumulIncome(e.getAccumulIncome().add(e.getYesterdayIncome()));
                        }else{
                            e.setAccumulIncome(e.getYesterdayIncome());
                        }
                        if(e.getAccumulReward() !=null){
                            e.setAccumulReward(e.getAccumulReward().add(e.getYesterdayIncome()).subtract(balanceDetail.getStaticsIncome()));
                        }else{
                            e.setAccumulReward(e.getYesterdayIncome().subtract(balanceDetail.getStaticsIncome()));
                        }
                        //总收入
                        if(e.getSumRevenue() !=null){
                            e.setSumRevenue(e.getSumRevenue().add(balanceDetail.getSumRevenue()));
                        }else {
                            e.setSumRevenue(balanceDetail.getSumRevenue());
                        }
                        if(balanceDetail.getSumRevenue() != null){
                            userCoinVolumeExService.updateIncome(null,balanceDetail.getSumRevenue(),balanceDetail.getUserId(),e.getCoinPlatSymbol(),false);
                        }
                        //昨日收入
                        e.setYesterdayRevenue(balanceDetail.getSumRevenue());
                        //团队刷单总值
                        e.setTeamAmount(balanceDetail.getTeamRecord());

                        //小区刷单市值
                        e.setTeamCommunityAmount(balanceDetail.getTeamCommunityRecord());
                        //社区级别
                        e.setTeamLevel("V"+String.valueOf(balanceDetail.getTeamLevel()));
                        //社区有效用户数
                        e.setValidNum(balanceDetail.getValidNum());
                        //一级邀请人数
                        e.setOneInvite(balanceDetail.getNodeNumber());
                        //刷单奖
                        if(e.getScalpingReward() != null){
                            e.setScalpingReward(e.getScalpingReward().add(balanceDetail.getStaticsIncome()));
                        }else {
                            e.setScalpingReward(balanceDetail.getStaticsIncome());
                        }
                        //分享奖
                        if(e.getShareReward()!= null){
                            e.setShareReward(e.getShareReward().add(balanceDetail.getDynamicsIncome()));
                        }else {
                            e.setShareReward(balanceDetail.getDynamicsIncome());
                        }
                        //社区管理奖
                        if(e.getCommunityManageReward() != null){
                            e.setCommunityManageReward(e.getCommunityManageReward().add(balanceDetail.getCommunityManageReward()));
                        }else {
                            e.setCommunityManageReward(balanceDetail.getCommunityManageReward());
                        }
                        //平级奖
                        if(e.getEqualityReward() != null){
                            e.setEqualityReward(e.getEqualityReward().add(balanceDetail.getEqualityReward()));
                        }else {
                            e.setEqualityReward(balanceDetail.getEqualityReward());
                        }
                        //级益奖
                        if(e.getDifferentialReward() != null){
                            e.setDifferentialReward(e.getDifferentialReward().add(balanceDetail.getCommunityManageReward()));
                        }else {
                            e.setDifferentialReward(balanceDetail.getCommunityManageReward());
                        }
                        e.setYesterdayStaticsIncome(balanceDetail.getStaticsIncome());
                        e.setYesterdayDynamicsIncome(balanceDetail.getDynamicsIncome());
                        e.setYesterdayCommunityReward(balanceDetail.getCommunityManageReward());
                        e.setYesterdayEqualityReward(balanceDetail.getEqualityReward());
                        balanceDetail.setVersion(0);
                        balanceUserCoinCountVolumeDao.updateById(e);
                        balanceUserCoinVolumeDetailDao.updateById(balanceDetail);
                    });
                }

            });
        }
        balanceUserCoinVolumeDetailDao.deleteByVersion();
    }
    @Override
    public void balanceIncomeDetail(){
        List<BalanceUserCoinVolume>  balanceUserCoinVolumeList= balanceUserCoinVolumeDao.findAll();

        if (CollectionUtils.isNotEmpty(balanceUserCoinVolumeList)) {
            balanceUserCoinVolumeList.forEach(e -> {
                List<PlatUser> platUserList= platUserDao.findInvitesById(e.getUserId());
                //动态收益和社区奖励计算 begin
                //动态收益
                BigDecimal   detailIncomeTotal=e.getCoinBalance().multiply(e.getDayRate());

                BigDecimal  dayRate=new BigDecimal(0);
                //社区奖励
                BigDecimal detailRewardTotal=new BigDecimal(0);

                if(CollectionUtils.isNotEmpty(platUserList)){
                    List<PlatUser> allPlatUserList=new ArrayList<PlatUser>();
                    allPlatUserList.addAll(platUserList);
                    treePlatUserList(platUserList,allPlatUserList);
                    Map<String,BigDecimal> detailRewardMap=new HashMap<String,BigDecimal>();
                    calRewardTotal(detailRewardMap,allPlatUserList,e);
                    int length=platUserList.size();
                   for (int i=0;i<length;i++) {
                       PlatUser platUser=platUserList.get(i);
                        List<BalanceUserCoinVolume> childList = balanceUserCoinVolumeDao.findByUserIdAndCoin(platUser.getId(),e.getCoinSymbol());
                        BalanceUserCoinVolume balanceTmp = null;
                        if (CollectionUtils.isNotEmpty(childList)) {
                            balanceTmp = childList.get(0);
                        }

                       if(balanceTmp != null){
                           detailIncomeTotal= detailIncomeTotal.add(balanceTmp.getCoinBalance().multiply(balanceTmp.getDayRate()).divide(new BigDecimal(2)));
                       }
                        if(length>=3){

                           PlatUser  platUserBen=  platUserDao.findById(e.getUserId());
                           if(platUserBen !=null && StringUtils.isNotEmpty( platUserBen.getReferId()) ){
                               List<BalanceUserCoinVolume> supList = balanceUserCoinVolumeDao.findByUserIdAndCoin(platUserBen.getReferId(),e.getCoinSymbol());
                               BalanceUserCoinVolume balanceSupTmp = null;
                               if (CollectionUtils.isNotEmpty(supList)) {
                                   balanceSupTmp = supList.get(0);
                               }
                               if(balanceSupTmp != null){
                                   detailIncomeTotal= detailIncomeTotal.add(balanceSupTmp.getCoinBalance().multiply(balanceSupTmp.getDayRate()).multiply(new BigDecimal(0.15)));//此处不好计算，因为此时的上级动态收益无法确定
                               }
                           }
                       }
                       if(length>5){
                           if( i<=length-5){
                               if(dayRate.compareTo(new BigDecimal(0.005))<0) {
                                   dayRate= dayRate.add(new BigDecimal(0.001));
                               }

                           }
                       }
                   }

                    if(detailRewardMap.get("communityBalance") != null){
                        if(length>=10 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(10000000))>=0 ){
                            //待确认
                        }else if(length>=10 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(5000000))>=0 ){
                            detailRewardTotal=  detailRewardTotal.add(detailRewardMap.get("communityIncome").multiply(new BigDecimal(0.20)));
                        }else if(length>=10 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(2000000))>=0 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(5000000))<0){
                            detailRewardTotal= detailRewardTotal.add(detailRewardMap.get("communityIncome").multiply(new BigDecimal(0.15)));
                        }else if(length>=10 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(500000))>=0 && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(2000000))<0){
                            detailRewardTotal= detailRewardTotal.add(detailRewardMap.get("communityIncome").multiply(new BigDecimal(0.1)));
                        }else if(length>=5  && detailRewardMap.get("communityBalance").compareTo(new BigDecimal(100000))>=0){
                                detailRewardTotal=detailRewardTotal.add(detailRewardMap.get("communityIncome").multiply(new BigDecimal(0.05)));
                        }
                    }

                }
                //动态收益和社区奖励计算 end


                BalanceUserCoinVolumeDetail balanceUserCoinVolumeDetail=new BalanceUserCoinVolumeDetail();
                String id = SnowFlake.createSnowFlake().nextIdString();
                balanceUserCoinVolumeDetail.setId(id);
                balanceUserCoinVolumeDetail.setCoinSymbol(e.getCoinSymbol());
                balanceUserCoinVolumeDetail.setUserId(e.getUserId());

                //收益明细总计
                balanceUserCoinVolumeDetail.setDetailIncome(detailIncomeTotal);

                //奖励算法
                balanceUserCoinVolumeDetail.setDetailReward(detailRewardTotal);
                //收益日期精确到天
                balanceUserCoinVolumeDetail.setIncomeDate(LocalDateTime.now().minusHours(1));
                balanceUserCoinVolumeDetail.setCreateDate(LocalDateTime.now());
                balanceUserCoinVolumeDetail.setUpdateDate(LocalDateTime.now());
                balanceUserCoinVolumeDetail.setVersion(1);
                balanceUserCoinVolumeDetailDao.insert(balanceUserCoinVolumeDetail);
            });
        }
    }
    public  void treePlatUserList(List<PlatUser> userList,List<PlatUser> allUserList) {
        for (PlatUser user : userList) {

            List<PlatUser> platList= platUserDao.findInvitesById(user.getId());
            //遍历出父id等于参数的id，add进子节点集合
            if (CollectionUtils.isNotEmpty(platList)) {
                //递归遍历下一级
                allUserList.addAll(platList);
                treePlatUserList(platList,allUserList);

            }
        }

    }

    public  void calRewardTotal(Map<String,BigDecimal> detailRewardMap, List<PlatUser> allUserList, BalanceUserCoinVolume userCoinVolume) {
        BigDecimal communityIncome=new BigDecimal(0);
        BigDecimal communityBalance=new BigDecimal(0);
        for (PlatUser user : allUserList) {
            List<BalanceUserCoinVolume> platBalanceList = balanceUserCoinVolumeDao.findByUserIdAndCoin(user.getId(),userCoinVolume.getCoinSymbol());
            BalanceUserCoinVolume balanceVolumeTmp = null;
            if (CollectionUtils.isNotEmpty(platBalanceList)) {
                balanceVolumeTmp = platBalanceList.get(0);
            }
            if(balanceVolumeTmp != null){
                communityIncome=communityIncome.add(balanceVolumeTmp.getCoinBalance().multiply(balanceVolumeTmp.getDayRate()));
                communityBalance= communityBalance.add(balanceVolumeTmp.getCoinBalance());
            }
        }
        detailRewardMap.put("communityIncome",communityIncome);
        detailRewardMap.put("communityBalance",communityBalance);

    }

    /**
     * 重新梳理收益规账新方法
     */
    @Override
    public void balanceIncomeDetailNew(Map<String ,BigDecimal> map,Map<String,TradePairVO>  tradePairMap){
        //静态收益、动态收益1和3 计算
        staticsIncomeAndPartDynamics(map,tradePairMap);

        //团队业绩、团队小区业绩、社区总收益 计算
        communityRecordAndLevel();

        //社区管理奖 计算
        calcManagementAward(map,tradePairMap);

        //动态收益2、平级奖 计算
        calcIncomeAndEqualAward(map);
    }

    public  void treeCommunityUserList(List<PlatUser> userList,Map<String,TradePairVO>  tradePairMap,List<BigDecimal> childSumRecordList, List validList,List<BigDecimal> childCommunityList,Map<String,BigDecimal> map,BigDecimal platPrice) {


        for (PlatUser user : userList) {
            List<BalanceUserCoinCountVolume> childUserVolumeList=balanceUserCoinCountVolumeDao.findByUserId(user.getId());
            BalanceUserCoinCountVolume balanceUserCountVolume=null;
            if(CollectionUtils.isNotEmpty(childUserVolumeList)){
                balanceUserCountVolume=childUserVolumeList.get(0);
            }
           BigDecimal childCoinBalance =BigDecimal.ZERO;
            if(balanceUserCountVolume != null){
                childCoinBalance=  childCoinBalance.add(balanceUserCountVolume.getCoinBalance());
            }
            if (childCoinBalance.compareTo(new BigDecimal(200)) >= 0) {
                validList.add("1");
            }
            childSumRecordList.add(childCoinBalance);
            BigDecimal balance=new BigDecimal(5000);
            BigDecimal balance2=new BigDecimal(1000);
            BigDecimal childRateSec=new BigDecimal(0);
            if(childCoinBalance.compareTo(balance)>0){
                childRateSec=map.get("threeDayRate");
            }else if (childCoinBalance.compareTo(balance2)>0){
                childRateSec=map.get("secondDayRate");
            } else{
                childRateSec=map.get("oneDayRate");
            }
//            if(platPrice.compareTo(BigDecimal.ZERO)>0){
//                childCommunityList.add(childCoinBalance.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(childRateSec));
//            }

                childCommunityList.add(childCoinBalance.multiply(childRateSec));

            List<PlatUser> platList= platUserDao.findInvitesById(user.getId());
            if (CollectionUtils.isNotEmpty(platList)) {
                treeCommunityUserList(platList, tradePairMap, childSumRecordList, validList,childCommunityList,map,platPrice);
            }
        }
    }
        public  boolean treeCommunityUserDetailList( List<PlatUser> userList) {
        for (PlatUser user : userList) {
            List<BalanceUserCoinVolumeDetail> childDetailList2 = balanceUserCoinVolumeDetailDao.findByVersionUserId(user.getId());
            if(CollectionUtils.isNotEmpty(childDetailList2)){
                for(BalanceUserCoinVolumeDetail  userCoinVolumeDetail:childDetailList2){
                    if (userCoinVolumeDetail.getTeamLevel()>0){
                        return true;
                    }
                }

            }

              List<PlatUser> platList= platUserDao.findInvitesById(user.getId());
            //遍历出父id等于参数的id，add进子节点集合
            if (CollectionUtils.isNotEmpty(platList)) {

                treeCommunityUserDetailList(platList);
            }
        }
        return false;
    }
    /**
     * 静态收益计算和动态收益1和3计算
     */
    public void staticsIncomeAndPartDynamics(Map<String ,BigDecimal> map,Map<String,TradePairVO>  tradePairMap){
        List<PlatUser> platUserList=balanceUserCoinCountVolumeDao.findPlatUserAll();
        List<BalanceUserCoinVolume>  balanceUserCoinVolumeList2= balanceUserCoinVolumeDao.findCoinAll();
        Map<String,List<BalanceUserCoinCountVolume>> balanceUserCoinVolumeMap=new HashMap<String,List<BalanceUserCoinCountVolume>>();
        if (CollectionUtils.isNotEmpty(balanceUserCoinVolumeList2)) {
            for(BalanceUserCoinVolume balanceUserCoinVolume: balanceUserCoinVolumeList2){
                BalanceUserCoinCountVolume balanceUserCoinCountVolume=new BalanceUserCoinCountVolume();
                BeanUtils.copyProperties(balanceUserCoinVolume, balanceUserCoinCountVolume);
                List<BalanceUserCoinCountVolume> balanceUserCoinCountVolumeList=balanceUserCoinVolumeMap.get(balanceUserCoinCountVolume.getUserId());
                if(CollectionUtils.isNotEmpty(balanceUserCoinCountVolumeList)){
                    balanceUserCoinCountVolumeList.add(balanceUserCoinCountVolume);
                }else{
                    balanceUserCoinCountVolumeList=new ArrayList<BalanceUserCoinCountVolume>();
                    balanceUserCoinCountVolumeList.add(balanceUserCoinCountVolume);
                    balanceUserCoinVolumeMap.put(balanceUserCoinCountVolume.getUserId(),balanceUserCoinCountVolumeList);
                }
            }

        }
        for(PlatUser platUser:platUserList){
            BalanceUserCoinCountVolume balanceCountVolume =new BalanceUserCoinCountVolume();
            List<BalanceUserCoinCountVolume> balanceCountList= balanceUserCoinCountVolumeDao.findByUserId(platUser.getId());
            if (CollectionUtils.isNotEmpty(balanceCountList)) {
                balanceCountVolume=balanceCountList.get(0);
            }
            List<BalanceUserCoinCountVolume>  countlist=balanceUserCoinVolumeMap.get(platUser.getId());
            BigDecimal coinBalance=BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(countlist)) {
                for(BalanceUserCoinCountVolume countVolume: countlist){
                    BigDecimal lastPrice=BigDecimal.ZERO;
                    BigDecimal coinCount=BigDecimal.ZERO;

                    TradePairVO tradePair=tradePairMap.get(countVolume.getCoinSymbol());

                    if(tradePair!=null && tradePair.getLatestPrice() != null){
                        lastPrice=tradePair.getLatestPrice();
                    }
                    if(countVolume.getCoinBalance() != null){
                        coinCount=lastPrice.multiply(countVolume.getCoinBalance());
                    }
                    if("USDT".equals(countVolume.getCoinSymbol()) && countVolume.getCoinBalance() != null){
                        coinCount=countVolume.getCoinBalance();
                    }
                    coinBalance=coinBalance.add(coinCount);
                }
            }
            balanceCountVolume.setCoinBalance(coinBalance);
            balanceCountVolume.setCoinSymbol("USDT");
            balanceCountVolume.setCoinPlatSymbol("MG");
            balanceCountVolume.setYesterdayIncome(BigDecimal.ZERO);
            balanceCountVolume.setYesterdayReward(BigDecimal.ZERO);
            if( org.apache.commons.lang.StringUtils.isNotEmpty(balanceCountVolume.getId()) &&  !"null".equals(balanceCountVolume.getId())){
                balanceUserCoinCountVolumeDao.updateById(balanceCountVolume);
            }else{
                String id = SnowFlake.createSnowFlake().nextIdString();
                balanceCountVolume.setId(id);
                balanceCountVolume.setCreateDate(LocalDateTime.now());
                balanceCountVolume.setUserId(platUser.getId());
                balanceCountVolume.setReferId(platUser.getReferId());
                balanceUserCoinCountVolumeDao.insert(balanceCountVolume);
            }
        }

        List<BalanceUserCoinCountVolume>  balanceUserCoinVolumeList= balanceUserCoinCountVolumeDao.findCoinAll();
        if (CollectionUtils.isNotEmpty(balanceUserCoinVolumeList)) {
            balanceUserCoinVolumeList.forEach(e->{
                //用户静态收益计算 begin
                BigDecimal   staticsIncomeTotal=new BigDecimal(0);
                //用户静态收益计算 end
                //动态收益 1 和3
                BigDecimal   dynamicsIncomeTotal=new BigDecimal(0);
                //动态收益3的静态收益增长率
                BigDecimal   dayRate=new BigDecimal(0);

                //社区静态总收益
                BigDecimal   communityStaticsIncome=new BigDecimal(0);

                BigDecimal platPrice=BigDecimal.ZERO;

                //社区静态总收益
                BigDecimal   oneLevelIncome=new BigDecimal(0);


                //直推节点个数
                int length=0;
                //仓位分界线
                BigDecimal balance=new BigDecimal(5000);
                BigDecimal balance2=new BigDecimal(1000);
                BigDecimal balance3=new BigDecimal(200);
                //社区有效用户数
                int vaildNum=0;
                TradePairVO tradePair=tradePairMap.get("MG");
                if(tradePair != null && tradePair.getLatestPrice() != null){
                    platPrice=tradePair.getLatestPrice();
                }
                //一级邀请人数
                int oneInvite=0;
                List<String> validList=new ArrayList<String>();
                List<BigDecimal> childTeamSumRecordList=new ArrayList<BigDecimal>();

                //社区挖矿总额
                BigDecimal teamSumRecord=BigDecimal.ZERO;
                //小区
                BigDecimal teamCommunitySumRecord=BigDecimal.ZERO;
                List<PlatUser> childPlatUserList = platUserDao.findInvitesById(e.getUserId());
                for(PlatUser  platUser:childPlatUserList) {
                    BigDecimal childTeamSumRecord = BigDecimal.ZERO;
                    List<BalanceUserCoinCountVolume> childUserVolumeList=balanceUserCoinCountVolumeDao.findByUserId(platUser.getId());
                    BalanceUserCoinCountVolume balanceUserCountVolume=null;
                    if(CollectionUtils.isNotEmpty(childUserVolumeList)){
                        balanceUserCountVolume=childUserVolumeList.get(0);
                    }
                    BigDecimal childCoinBalance=BigDecimal.ZERO;
                    BigDecimal childRateSec=BigDecimal.ZERO;
                    if(balanceUserCountVolume != null){
                        childCoinBalance=childCoinBalance.add(balanceUserCountVolume.getCoinBalance());
                    }
                    if(childCoinBalance.compareTo(new BigDecimal(200))>=0){
                        oneInvite++;
                        validList.add("1");
                    }
                    if(childCoinBalance.compareTo(balance)>0){
                        childRateSec=map.get("threeDayRate");
                    }else if (childCoinBalance.compareTo(balance2)>0){
                        childRateSec=map.get("secondDayRate");
                    } else if(childCoinBalance.compareTo(balance3)>=0){
                        childRateSec=map.get("oneDayRate");
                    }
                    if(platPrice.compareTo(BigDecimal.ZERO)>0 ) {
                        dynamicsIncomeTotal = dynamicsIncomeTotal.add(childCoinBalance.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(childRateSec).multiply(new BigDecimal(0.5)));
                        communityStaticsIncome = communityStaticsIncome.add(childCoinBalance.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(childRateSec));
                    }

//                    dynamicsIncomeTotal = dynamicsIncomeTotal.add(childCoinBalance.multiply(childRateSec).multiply(new BigDecimal(0.5)));
//                    communityStaticsIncome = communityStaticsIncome.add(childCoinBalance.multiply(childRateSec));

                    childTeamSumRecord= childTeamSumRecord.add(childCoinBalance);

                    List<PlatUser> childAllPlatUserList = platUserDao.findInvitesById(platUser.getId());
                    List<BigDecimal> childSumRecordList=new ArrayList<BigDecimal>();
                    List<BigDecimal> childCommunityList=new ArrayList<BigDecimal>();
                    if(CollectionUtils.isNotEmpty(childAllPlatUserList)){
                        treeCommunityUserList(childAllPlatUserList, tradePairMap,childSumRecordList, validList,childCommunityList,map,platPrice);
                    }
                    if(CollectionUtils.isNotEmpty(childSumRecordList)){
                        for(BigDecimal childSum:childSumRecordList){
                            childTeamSumRecord= childTeamSumRecord.add(childSum);
                        }
                    }
                    if(CollectionUtils.isNotEmpty(childCommunityList)){
                        for(BigDecimal childSum:childCommunityList){
                            communityStaticsIncome= communityStaticsIncome.add(childSum);
                        }
                    }
                    teamSumRecord=teamSumRecord.add(childTeamSumRecord);
                    childTeamSumRecordList.add(childTeamSumRecord);
                }
                BigDecimal childMax=BigDecimal.ZERO;
                if(CollectionUtils.isNotEmpty(childTeamSumRecordList)){
                    childMax= Collections.max(childTeamSumRecordList);
                }
                teamCommunitySumRecord=teamCommunitySumRecord.add(teamSumRecord.subtract(childMax));
                vaildNum=validList.size();

                    if(oneInvite>5){
                        int rateInt=oneInvite-5;
                        if(rateInt>5){
                            rateInt=5;
                        }
                        BigDecimal addRate=new BigDecimal(0.0005);
                        dayRate= dayRate.add(addRate.multiply(new BigDecimal(rateInt)));
                    }

                //静态收益
                //收益率通过配置计算
                BigDecimal selfCoinBalance=e.getCoinBalance();
                if(platPrice.compareTo(BigDecimal.ZERO)>0) {
                    if (selfCoinBalance.compareTo(balance) > 0) {
                        staticsIncomeTotal = staticsIncomeTotal.add(selfCoinBalance.divide(platPrice, 16, BigDecimal.ROUND_HALF_UP).multiply(map.get("threeDayRate")));
                    } else if (selfCoinBalance.compareTo(balance2) > 0) {
                        staticsIncomeTotal = staticsIncomeTotal.add(selfCoinBalance.divide(platPrice, 16, BigDecimal.ROUND_HALF_UP).multiply(map.get("secondDayRate")));
                    } else if (selfCoinBalance.compareTo(balance3) >= 0){
                        staticsIncomeTotal = staticsIncomeTotal.add(selfCoinBalance.divide(platPrice, 16, BigDecimal.ROUND_HALF_UP).multiply(map.get("oneDayRate")));
                    }
                }

//
//                    if(selfCoinBalance.compareTo(balance)>0){
//                        staticsIncomeTotal=staticsIncomeTotal.add(selfCoinBalance.multiply(map.get("threeDayRate")));
//                    }else if (selfCoinBalance.compareTo(balance2)>0){
//                        staticsIncomeTotal=staticsIncomeTotal.add(selfCoinBalance.multiply(map.get("secondDayRate")));
//                    }else{
//                        staticsIncomeTotal=staticsIncomeTotal.add(selfCoinBalance.multiply(map.get("oneDayRate")));
//                    }


                List<BalanceChangeUserCoinVolume> balanceChangeList=balanceChangeUserCoinVolumeDao.findChangeByUserId(e.getUserId());
                if (CollectionUtils.isNotEmpty(balanceChangeList)) {
                    for(BalanceChangeUserCoinVolume balanceChangeVolume : balanceChangeList ){
                        BigDecimal changeIncome=new BigDecimal(0);
                        if(platPrice.compareTo(BigDecimal.ZERO)>0) {
                            TradePairVO tradeChangePair=tradePairMap.get(balanceChangeVolume.getCoinSymbol());
                           BigDecimal coinNum= balanceChangeVolume.getCoinNum();
                            if(tradeChangePair != null && tradeChangePair.getLatestPrice() != null){
                                coinNum=coinNum.multiply(tradePair.getLatestPrice());
                            }
                            BigDecimal  changeCoinBalance=e.getCoinBalance();
                            if(changeCoinBalance.compareTo(balance)>0){
                                changeIncome=changeIncome.add(coinNum.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(map.get("threeDayRate")));
                            }else if (changeCoinBalance.compareTo(balance2)>0){
                                changeIncome=changeIncome.add(coinNum.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(map.get("secondDayRate")));
                            } else if (changeCoinBalance.compareTo(balance3)>0){
                                changeIncome=changeIncome.add(coinNum.divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(map.get("oneDayRate")));
                            }
                        }
                       if(balanceChangeVolume.getAccumulIncome() !=null){
                           balanceChangeVolume.setAccumulIncome(balanceChangeVolume.getAccumulIncome().add(changeIncome));
                       }else {
                           balanceChangeVolume.setAccumulIncome(changeIncome);
                       }
                        balanceChangeUserCoinVolumeDao.updateById(balanceChangeVolume);
                    }
                }
                if(platPrice.compareTo(BigDecimal.ZERO)>0) {
                    staticsIncomeTotal=staticsIncomeTotal.add(e.getCoinBalance().divide(platPrice,16,BigDecimal.ROUND_HALF_UP ).multiply(dayRate));
                }
                BalanceUserCoinVolumeDetail balanceUserCoinVolumeDetail=new BalanceUserCoinVolumeDetail();
                String id = SnowFlake.createSnowFlake().nextIdString();
                balanceUserCoinVolumeDetail.setId(id);
                balanceUserCoinVolumeDetail.setCoinSymbol(e.getCoinSymbol());
                balanceUserCoinVolumeDetail.setUserId(e.getUserId());

                balanceUserCoinVolumeDetail.setStaticsIncome(staticsIncomeTotal);
                balanceUserCoinVolumeDetail.setDynamicsIncome(dynamicsIncomeTotal);
                balanceUserCoinVolumeDetail.setTeamRecord(teamSumRecord);
                balanceUserCoinVolumeDetail.setCommunityStaticsIncome(communityStaticsIncome);
                balanceUserCoinVolumeDetail.setNodeNumber(oneInvite);

                //社区有效用户数
                balanceUserCoinVolumeDetail.setValidNum(vaildNum);

                //收益明细总计
                balanceUserCoinVolumeDetail.setDetailIncome(new BigDecimal(0));

                //奖励算法
                balanceUserCoinVolumeDetail.setDetailReward(new BigDecimal(0));
                balanceUserCoinVolumeDetail.setCommunityManageReward(new BigDecimal(0));
                balanceUserCoinVolumeDetail.setEqualityReward(new BigDecimal(0));
                balanceUserCoinVolumeDetail.setLevelDifferenceReward(new BigDecimal(0));
                balanceUserCoinVolumeDetail.setSumRevenue(new BigDecimal(0));
                balanceUserCoinVolumeDetail.setTeamCommunityRecord(teamCommunitySumRecord);
               // balanceUserCoinVolumeDetail.setTeamCoinRecord(teamCoinRecord);
                balanceUserCoinVolumeDetail.setOneLevelIncome(oneLevelIncome);
                balanceUserCoinVolumeDetail.setReferId(e.getReferId());
                //收益日期精确到天
                balanceUserCoinVolumeDetail.setIncomeDate(LocalDateTime.now());
                balanceUserCoinVolumeDetail.setCreateDate(LocalDateTime.now());
                balanceUserCoinVolumeDetail.setUpdateDate(LocalDateTime.now());
                balanceUserCoinVolumeDetail.setVersion(1);
                balanceUserCoinVolumeDetailDao.insert(balanceUserCoinVolumeDetail);
            });

        }

    }


    /**
     * 计算小区业绩、等级
     */
    public void communityRecordAndLevel(){
        List<BalanceUserCoinVolumeDetail>  balanceVolumeDetailList=balanceUserCoinVolumeDetailDao.findAll();
        if (CollectionUtils.isNotEmpty(balanceVolumeDetailList)) {
            balanceVolumeDetailList.forEach(e ->{
                //社区等级
                int teamLevel=0;
                //直推节点
                int length=e.getNodeNumber();

                BigDecimal teamRecord=e.getTeamRecord();
                BigDecimal  teamCommunityRecord=e.getTeamCommunityRecord();
                if(teamRecord != null){
                    if(length>=10 && teamCommunityRecord !=null && teamCommunityRecord.compareTo(new BigDecimal(10000000))>=0 ){
                        teamLevel=5;
                    }else if(length>=10 && teamCommunityRecord !=null && teamCommunityRecord.compareTo(new BigDecimal(5000000))>=0 && teamCommunityRecord.compareTo(new BigDecimal(10000000))<0 ){
                        teamLevel=4;
                    }else if(length>=10 && teamCommunityRecord !=null && teamCommunityRecord.compareTo(new BigDecimal(1000000))>=0 && teamCommunityRecord.compareTo(new BigDecimal(5000000))<0){
                        teamLevel=3;
                    }else if(length>=10 && teamCommunityRecord !=null && teamCommunityRecord.compareTo(new BigDecimal(300000))>=0 && teamCommunityRecord.compareTo(new BigDecimal(1000000))<0){
                        teamLevel=2;
                    }else if(length>=5  && teamRecord.compareTo(new BigDecimal(100000))>=0){
                        teamLevel=1;
                    }
                }
                e.setTeamLevel(teamLevel);
                balanceUserCoinVolumeDetailDao.updateById(e);
            });

        }
    }
    /**
     * 计算社区管理奖
     */
    public void  calcManagementAward(Map<String ,BigDecimal> map,Map<String,TradePairVO>  tradePairMap) {
        List<BalanceUserCoinVolumeDetail> balanceVolumeDetailList = balanceUserCoinVolumeDetailDao.findAll();
        //全网络静态收益加权平均分红
        Map<String, BigDecimal> goalStaticsIncomeMap = new HashMap<String, BigDecimal>();
        List<Coin> coinList = coinDao.findAll();
        TradePairVO tradePair = tradePairMap.get("MG");
        BigDecimal platPrice = BigDecimal.ZERO;
        if (tradePair != null && tradePair.getLatestPrice() != null) {
            platPrice = tradePair.getLatestPrice();
        }

        List<BalanceUserCoinVolumeDetail> detailGoalist = balanceUserCoinVolumeDetailDao.findAll();
        BigDecimal coinStIncome = new BigDecimal(0);
        //V5等级个数
        int len = 0;
        if (CollectionUtils.isNotEmpty(detailGoalist)) {
            for (BalanceUserCoinVolumeDetail balanceUserCoinVolume : detailGoalist) {
                coinStIncome = coinStIncome.add(balanceUserCoinVolume.getStaticsIncome());
                if (balanceUserCoinVolume.getTeamLevel() == 5) {
                    len++;
                }
            }

        }
        if (len != 0) {
            coinStIncome = coinStIncome.multiply(new BigDecimal(0.05)).divide(new BigDecimal(len));
        }

        List<BalanceUserCoinVolumeDetail> balanceVolumeDetailList2 = balanceUserCoinVolumeDetailDao.findAll();
        //社区管理奖
        if (CollectionUtils.isNotEmpty(balanceVolumeDetailList2)) {
            for (BalanceUserCoinVolumeDetail volumeDetail : balanceVolumeDetailList2) {
                List<BigDecimal> countList = new ArrayList<>();
                BigDecimal communityMagageReward = BigDecimal.ZERO;
                BigDecimal communityStaticIncome = BigDecimal.ZERO;
                if (volumeDetail.getTeamLevel() != 0) {
                    treeCalcUserDetailRewardList(volumeDetail, volumeDetail, countList);
                }
                if (CollectionUtils.isNotEmpty(countList)) {
                    for (BigDecimal countDetail : countList) {
                        communityMagageReward = communityMagageReward.add(countDetail);
                        communityStaticIncome=communityStaticIncome.add(countDetail);
                    }
                }
                if (volumeDetail.getTeamLevel() > 0) {
                    int teamLevel = volumeDetail.getTeamLevel();
                    if (teamLevel == 5) {
                        communityMagageReward = communityMagageReward.multiply(new BigDecimal(0.30));
                        communityMagageReward = communityMagageReward.add(coinStIncome);
                    } else if (teamLevel == 4) {
                        communityMagageReward = communityMagageReward.multiply(new BigDecimal(0.25));
                    } else if (teamLevel == 3) {
                        communityMagageReward = communityMagageReward.multiply(new BigDecimal(0.20));
                    } else if (teamLevel == 2) {
                        communityMagageReward = communityMagageReward.multiply(new BigDecimal(0.15));
                    } else if (teamLevel == 1) {
                        communityMagageReward = communityMagageReward.multiply(new BigDecimal(0.10));
                    }
                    volumeDetail.setCommunityManageReward(communityMagageReward);
                    volumeDetail.setCommunityStaticsIncome(communityStaticIncome);
                    balanceUserCoinVolumeDetailDao.updateById(volumeDetail);
                }
            }
        }
        //级差奖
        List<BalanceUserCoinVolumeDetail> balanceVolumeDetailList3 = balanceUserCoinVolumeDetailDao.findAll();
        if (CollectionUtils.isNotEmpty(balanceVolumeDetailList3)) {
            for (BalanceUserCoinVolumeDetail volumeDetail : balanceVolumeDetailList3) {
                if (volumeDetail.getTeamLevel() >= 2) {
                    Map<String,BalanceUserCoinVolumeDetail> countMap =new HashMap<String,BalanceUserCoinVolumeDetail>();
                    treeCalcUserDiffRewardList(volumeDetail, volumeDetail,null,true,countMap );
                    if (countMap.size()>0) {
                        BigDecimal countDiffReward=BigDecimal.ZERO;
                          for(String key:countMap.keySet()){
                              BalanceUserCoinVolumeDetail  coinVolumeDetail=countMap.get(key);
                              int chaLevel= volumeDetail.getTeamLevel()-coinVolumeDetail.getTeamLevel();
                              countDiffReward=countDiffReward.add(coinVolumeDetail.getCommunityStaticsIncome().multiply(new BigDecimal(0.05)).multiply(new BigDecimal(chaLevel)));
                          }
                        volumeDetail.setLevelDifferenceReward(countDiffReward);
                        balanceUserCoinVolumeDetailDao.updateById(volumeDetail);
                    }
                }
            }
        }

        //平级奖
        List<BalanceUserCoinVolumeDetail> balanceVolumeDetailList4 = balanceUserCoinVolumeDetailDao.findAll();
        if (CollectionUtils.isNotEmpty(balanceVolumeDetailList4)) {
            for (BalanceUserCoinVolumeDetail volumeDetail : balanceVolumeDetailList4) {
                if (volumeDetail.getTeamLevel() >= 1) {
                    treeCalcUserEequalRewardList(volumeDetail, volumeDetail,map);
                    balanceUserCoinVolumeDetailDao.updateById(volumeDetail);
                }
            }
        }
    }
    public  void treeCalcUserDetailRewardList(BalanceUserCoinVolumeDetail userDetail,BalanceUserCoinVolumeDetail userSelfDetail,List<BigDecimal> countList) {
        if (userDetail !=null) {
            List<BalanceUserCoinVolumeDetail>   childDetailList=    balanceUserCoinVolumeDetailDao.findAllByReferId(userDetail.getUserId(),"USDT");
            if(CollectionUtils.isNotEmpty(childDetailList)){
               for(BalanceUserCoinVolumeDetail childDetail:childDetailList){
                   if(childDetail.getTeamLevel() != 0){
                       continue;
                   }
                   countList.add(childDetail.getStaticsIncome());
                   treeCalcUserDetailRewardList(childDetail,userSelfDetail,countList);
               }
            }

        }
    }

    public  void treeCalcUserDiffRewardList(BalanceUserCoinVolumeDetail userDetail,BalanceUserCoinVolumeDetail userSelfDetail,BalanceUserCoinVolumeDetail chuanDetail,boolean flag, Map<String,BalanceUserCoinVolumeDetail> countMap ) {
        if (userDetail !=null) {
            List<BalanceUserCoinVolumeDetail>   childDetailList=    balanceUserCoinVolumeDetailDao.findAllByReferId(userDetail.getUserId(),"USDT");
            if(CollectionUtils.isNotEmpty(childDetailList)){
                for(BalanceUserCoinVolumeDetail childDetail:childDetailList){
                    if(childDetail.getTeamLevel()>=userSelfDetail.getTeamLevel()){
                        continue;
                    }
                   if(!flag){
                       if(childDetail.getTeamLevel()<=chuanDetail.getTeamLevel()){
                           chuanDetail.setCommunityStaticsIncome(chuanDetail.getCommunityStaticsIncome().add(childDetail.getCommunityStaticsIncome()));
                           treeCalcUserDiffRewardList(childDetail,userSelfDetail,chuanDetail,false,countMap);

                       }  else {
                           countMap.put(chuanDetail.getUserId(),chuanDetail);
                           treeCalcUserDiffRewardList(childDetail,userSelfDetail,childDetail,false,countMap);
                       }
                   }else treeCalcUserDiffRewardList(childDetail,userSelfDetail,childDetail,false,countMap);

                }
            }else{
                if(chuanDetail !=null){
                    countMap.put(chuanDetail.getUserId(),chuanDetail);
                }

            }

        }
    }
    public  void treeCalcUserEequalRewardList(BalanceUserCoinVolumeDetail userDetail,BalanceUserCoinVolumeDetail userSelfDetail,Map<String,BigDecimal> map) {
        if (userDetail !=null) {
            List<BalanceUserCoinVolumeDetail>   childDetailList=    balanceUserCoinVolumeDetailDao.findAllByReferId(userDetail.getUserId(),"USDT");
            if(CollectionUtils.isNotEmpty(childDetailList)){
                for(BalanceUserCoinVolumeDetail childDetail:childDetailList){
                    if(childDetail.getTeamLevel()==userSelfDetail.getTeamLevel()){
                            //平级奖
                        BigDecimal userIncome = childDetail.getDynamicsIncome().add(childDetail.getStaticsIncome()).add(childDetail.getLevelDifferenceReward()).add(childDetail.getCommunityManageReward());
                        userIncome=userIncome.multiply(map.get("equalReward"));
                        userSelfDetail.setEqualityReward(userSelfDetail.getEqualityReward().add(userIncome));
                        continue;
                    }
                    treeCalcUserEequalRewardList(childDetail,userSelfDetail,map);
                }
            }

        }
    }


    /**
     * 动态收益2
     */
    public void  calcIncomeAndEqualAward(Map<String ,BigDecimal> map){
        //动态收益2，从最顶层计算，依次类推
        List<BalanceUserCoinVolumeDetail>  suprerDetailList= balanceUserCoinVolumeDetailDao.findSuprer();
        if (CollectionUtils.isNotEmpty(suprerDetailList)) {
            suprerDetailList.forEach(e->{
                e.setDetailIncome(e.getStaticsIncome().add(e.getDynamicsIncome()));
                e.setDetailReward(e.getCommunityManageReward().add(e.getEqualityReward()).add(e.getLevelDifferenceReward()));
                e.setSumRevenue(e.getDetailIncome().add(e.getDetailReward()));
                balanceUserCoinVolumeDetailDao.updateById(e);
                BigDecimal sumIncome=e.getDetailIncome().add(e.getDetailReward());
                List<BalanceUserCoinVolumeDetail>  childDetailList = balanceUserCoinVolumeDetailDao.findAllByReferId(e.getUserId(),e.getCoinSymbol());
                if (CollectionUtils.isNotEmpty(childDetailList)) {
                    treeSuperUserList(childDetailList,sumIncome);
                }
            });
        }
    }

    /**
     * 计算动态收益的上级收益加权平均分红
     * @param userList
     * @param sumIncome
     */
    public  void treeSuperUserList(List<BalanceUserCoinVolumeDetail> userList,BigDecimal sumIncome) {
        int length=0;
        List<BalanceUserCoinVolumeDetail> userList2=new ArrayList<BalanceUserCoinVolumeDetail>();
        for (BalanceUserCoinVolumeDetail user : userList) {
            if(user.getValidNum()>=3){
                length++;
                userList2.add(user);
            }
        }
        for (BalanceUserCoinVolumeDetail user : userList) {
            if(length>0 && user.getValidNum()>=3){
                user.setDynamicsIncome(user.getDynamicsIncome().add(sumIncome.multiply(new BigDecimal(0.15)).divide(new BigDecimal(length),16,BigDecimal.ROUND_HALF_UP )));
            }
            user.setDetailIncome(user.getStaticsIncome().add(user.getDynamicsIncome()));
            user.setDetailReward(user.getCommunityManageReward().add(user.getEqualityReward()).add(user.getLevelDifferenceReward()));
            user.setSumRevenue(user.getDetailIncome().add(user.getDetailReward()));
            balanceUserCoinVolumeDetailDao.updateById(user);
            List<BalanceUserCoinVolumeDetail> platList= balanceUserCoinVolumeDetailDao.findAllByReferId(user.getUserId(),user.getCoinSymbol());
            //遍历出父id等于参数的id，add进子节点集合
            if (CollectionUtils.isNotEmpty(platList)) {
                //递归遍历下一级
                //allUserList.addAll(platList);
                treeSuperUserList(platList,user.getDetailIncome().add(user.getCommunityManageReward()));
            }
        }

    }

    /**
     * 平级奖查找对应节点
     * @param userList
     * @param level
     * @return
     */
    public  List<BalanceUserCoinVolumeDetail>  treeChildEqualUserList(List<BalanceUserCoinVolumeDetail> userList,int level) {
        List<BalanceUserCoinVolumeDetail> equalList=new ArrayList<BalanceUserCoinVolumeDetail>();
        for (BalanceUserCoinVolumeDetail user : userList) {
            if(user.getTeamLevel()==level){
                equalList.add(user);
            }
        }
        if(equalList.size()>0){
            return equalList;
        }
        List<BalanceUserCoinVolumeDetail> childList=new ArrayList<BalanceUserCoinVolumeDetail>();
        for (BalanceUserCoinVolumeDetail user : userList) {

            List<BalanceUserCoinVolumeDetail> platList= balanceUserCoinVolumeDetailDao.findAllByReferId(user.getUserId(),user.getCoinSymbol());
            //遍历出父id等于参数的id，add进子节点集合
            if (CollectionUtils.isNotEmpty(platList)) {
                //递归遍历下一级
                //allUserList.addAll(platList);
                childList.addAll(platList);
            }
        }
        if(childList.size()==0){
            return childList;
        }
        treeChildEqualUserList(childList,level);
       return equalList;
    }

/***
 *
 */
  @Override
  public void  balanceJackpotIncomeCount(Map<String,TradePairVO>  tradePairMap){
      List<BalanceUserCoinVolume> listVolume = balanceUserCoinVolumeDao.findByAllRank();
      Map<String,List<BalanceUserCoinVolume>> balanceUserCoinVolumeMap=new HashMap<String,List<BalanceUserCoinVolume>>();
      if (CollectionUtils.isNotEmpty(listVolume)) {
          for(BalanceUserCoinVolume balanceUserCoinVolume: listVolume){
              List<BalanceUserCoinVolume> balanceUserCoinCountVolumeList=balanceUserCoinVolumeMap.get(balanceUserCoinVolume.getUserId());
              if(CollectionUtils.isNotEmpty(balanceUserCoinCountVolumeList)){
                  balanceUserCoinCountVolumeList.add(balanceUserCoinVolume);
              }else{
                  balanceUserCoinCountVolumeList=new ArrayList<BalanceUserCoinVolume>();
                  balanceUserCoinCountVolumeList.add(balanceUserCoinVolume);
                  balanceUserCoinVolumeMap.put(balanceUserCoinVolume.getUserId(),balanceUserCoinCountVolumeList);
              }
          }
      }
      List<BalanceUserCoinVolume> rankList=new ArrayList<BalanceUserCoinVolume>();
      for(String key:balanceUserCoinVolumeMap.keySet()) {
          BigDecimal childTeamSumRecord = BigDecimal.ZERO;
          List<BalanceUserCoinVolume> countlist = balanceUserCoinVolumeMap.get(key);
          BigDecimal childCoinBalance = BigDecimal.ZERO;
          if (CollectionUtils.isNotEmpty(countlist)) {
              BalanceUserCoinVolume countVolume=new BalanceUserCoinVolume();
              BalanceUserCoinVolume countVolume2=countlist.get(0);
              countVolume.setId(countVolume2.getUserId());
              countVolume.setUserId(countVolume2.getUserId());
              countVolume.setMobile(countVolume2.getMobile());
              countVolume.setMail(countVolume2.getMail());
              for (BalanceUserCoinVolume countVolume3 : countlist) {
                  BigDecimal lastPrice = BigDecimal.ZERO;
                  BigDecimal coinCount = countVolume3.getCoinBalance();
                  TradePairVO tradePair = tradePairMap.get(countVolume3.getCoinSymbol());
                  if (tradePair != null && tradePair.getLatestPrice() != null) {
                      coinCount = lastPrice.multiply(countVolume3.getCoinBalance());
                  }
                  childCoinBalance = childCoinBalance.add(coinCount);
              }
              countVolume.setCoinBalance(childCoinBalance);
              rankList.add(countVolume);
          }
      }
      rankList.sort((o1, o2) -> o2.getCoinBalance().compareTo(o1.getCoinBalance()));
      List<BalancePlatJackpotVolumeDetail> jackpotVolumeList=balancePlatJackpotVolumeDetailDao.findByUserIdAndCoin("MG")  ;
      if(CollectionUtils.isNotEmpty(jackpotVolumeList)){
          BalancePlatJackpotVolumeDetail jackpotVolume=jackpotVolumeList.get(0);
          BigDecimal allIncome=jackpotVolume.getAllCoinIncome();
          BigDecimal subIncome=BigDecimal.ZERO;
          if(allIncome != null && allIncome.compareTo(BigDecimal.ZERO)>0){
              if(CollectionUtils.isNotEmpty(rankList)){
                 int len=rankList.size();
                  if(len>10){
                      len=10;
                  }
                  for(int i=0;i<len;i++){
                      BalanceUserCoinVolume countVolume=rankList.get(i);
                      BigDecimal avgIncome=BigDecimal.ZERO;
                      if(i==0){
                          avgIncome=allIncome.multiply(new BigDecimal(0.1));
                          subIncome=subIncome.add(avgIncome);
                      }else if(i==1){
                          avgIncome=allIncome.multiply(new BigDecimal(0.06));
                          subIncome=subIncome.add(avgIncome);
                      }else if(i==2){
                          avgIncome=allIncome.multiply(new BigDecimal(0.04));
                          subIncome=subIncome.add(avgIncome);
                      }else if(i==3){
                          avgIncome=allIncome.multiply(new BigDecimal(0.02));
                          subIncome=subIncome.add(avgIncome);
                      }else{
                          avgIncome=allIncome.multiply(new BigDecimal(0.005));
                          subIncome=subIncome.add(avgIncome);
                      }
                      userCoinVolumeExService.updateIncome(null,avgIncome,countVolume.getUserId(),jackpotVolume.getCoinSymbol(),false);
                  }

              }
          }
          jackpotVolume.setAllCoinIncome(allIncome.subtract(subIncome));
          LocalDateTime localTime=LocalDateTime.now();
          jackpotVolume.setRewardDate(localTime.plusDays(10));
          balancePlatJackpotVolumeDetailDao.updateById(jackpotVolume);
      }



  }

}
