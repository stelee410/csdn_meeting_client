package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * 会议数据统计（agent.prd §2.6）
 * basic 全量；advanced 受 isPremium 控制
 */
public class MeetingStatisticsDTO {

    private BasicStats basic;
    private Object advanced;  // 用户画像聚合，isPremium 时返回
    private boolean premiumRequired;

    public static class BasicStats {
        private long pv;
        private long uv;
        private long clicks;
        private long registrations;
        private long checkins;
        private double checkinRate;
        private double exposureTrend;
        private double clicksTrend;
        private double conversionRate;
        private List<TrendItem> trend7d;

        public long getPv() { return pv; }
        public void setPv(long pv) { this.pv = pv; }
        public long getUv() { return uv; }
        public void setUv(long uv) { this.uv = uv; }
        public long getClicks() { return clicks; }
        public void setClicks(long clicks) { this.clicks = clicks; }
        public long getRegistrations() { return registrations; }
        public void setRegistrations(long registrations) { this.registrations = registrations; }
        public long getCheckins() { return checkins; }
        public void setCheckins(long checkins) { this.checkins = checkins; }
        public double getCheckinRate() { return checkinRate; }
        public void setCheckinRate(double checkinRate) { this.checkinRate = checkinRate; }
        public double getExposureTrend() { return exposureTrend; }
        public void setExposureTrend(double exposureTrend) { this.exposureTrend = exposureTrend; }
        public double getClicksTrend() { return clicksTrend; }
        public void setClicksTrend(double clicksTrend) { this.clicksTrend = clicksTrend; }
        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
        public List<TrendItem> getTrend7d() { return trend7d; }
        public void setTrend7d(List<TrendItem> trend7d) { this.trend7d = trend7d; }
    }

    public static class TrendItem {
        private String date;
        private long pv;
        private long registrations;

        public TrendItem() {}
        public TrendItem(String date, long pv, long registrations) {
            this.date = date;
            this.pv = pv;
            this.registrations = registrations;
        }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public long getPv() { return pv; }
        public void setPv(long pv) { this.pv = pv; }
        public long getRegistrations() { return registrations; }
        public void setRegistrations(long registrations) { this.registrations = registrations; }
    }

    public BasicStats getBasic() { return basic; }
    public void setBasic(BasicStats basic) { this.basic = basic; }
    public Object getAdvanced() { return advanced; }
    public void setAdvanced(Object advanced) { this.advanced = advanced; }
    public boolean isPremiumRequired() { return premiumRequired; }
    public void setPremiumRequired(boolean premiumRequired) { this.premiumRequired = premiumRequired; }
}
