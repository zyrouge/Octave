package xyz.gnarbot.gnar.tests;

public class HTTPPosting {
//
//    @Test
//    public void printRecommendedShardCount()  {
//        Request request = new Request.Builder()
//                .url(Requester.DISCORD_API_PREFIX + "gateway/bot")
//                .header("User-Agent", "Gnar Bot")
//                .header("Authorization", "Bot " + Credentials.INSTANCE.getProduction())
//                .get()
//                .build();
//
//        try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
//            JSONObject obj = new JSONObject(response.body().string());
//            System.out.println(obj.getInt("shards"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void update() {
//        int count = 32500;
//
//        updateAbalCount(count);
//        updateCarbonitexCount(count);
//        updateDiscordBotsCount(count);
//    }
//
//    private void updateAbalCount(int i) {
//        JSONObject json = new JSONObject().put("server_count", i);
//
//        Request request = new Request.Builder()
//                .url("https://bots.discord.pw/api/bots/201503408652419073/stats")
//                .header("User-Agent", "Gnar Bot")
//                .header("Authorization", Credentials.INSTANCE.getAbal())
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
//                .build();
//
////        try (Response response = Requester.CLIENT.newCall(request).execute()) {
////            System.out.println("Abal | Response code: " + response.code());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println(response.code());
//            }
//        });
//    }
//
//    private void updateDiscordBotsCount(int i) {
//        JSONObject json = new JSONObject().put("server_count", i);
//
//        Request request = new Request.Builder()
//                .url("https://discordbots.org/api/bots/201503408652419073/stats")
//                .header("User-Agent", "Gnar Bot")
//                .header("Authorization", Credentials.INSTANCE.getDiscordBots())
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
//                .build();
//
//        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println(response.code());
//            }
//        });
//    }
//
//    private void updateCarbonitexCount(int i) {
//        JSONObject json = new JSONObject().put("key", Credentials.INSTANCE.getCarbonitex()).put("servercount", i);
//
//        Request request = new Request.Builder()
//                .url("https://www.carbonitex.net/discord/data/botdata.php")
//                .header("User-Agent", "Gnar Bot")
//                .header("Authorization", Credentials.INSTANCE.getAbal())
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
//                .build();
//
//        HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println(response.code());
//            }
//        });
//    }
}
