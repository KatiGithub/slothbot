package com.github.princesslana.slothbot;

import com.eclipsesource.json.JsonObject;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import disparse.discord.smalld.DiscordRequest;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Discord {
  private Discord() {}

  public static String getChannelId(DiscordRequest req) {
    return req.getDispatcher().channelFromEvent(req.getEvent());
  }

  public static String getGuildId(DiscordRequest req) {
    return req.getDispatcher().guildFromEvent(req.getEvent());
  }

  public static Optional<String> getGuild(com.google.gson.JsonObject payload) {
    return Optional.ofNullable(payload.get("guild_id")).map(JsonElement::getAsString);
  }

  public static Set<String> getRoles(com.google.gson.JsonObject payload) {
    return Optional.ofNullable(payload.get("member"))
        .map(JsonElement::getAsJsonObject)
        .map(inner -> inner.get("roles"))
        .map(JsonElement::getAsJsonArray)
        .map(JsonArray::iterator)
        .map(ImmutableSet::copyOf)
        .map(s -> s.stream().map(JsonElement::getAsString).collect(Collectors.toUnmodifiableSet()))
        .orElse(Set.of());
  }

  public static void ifEvent(JsonObject json, String evt, Consumer<JsonObject> f) {
    var isEvent = json.getInt("op", -1) == 0 && json.getString("t", "").equals(evt);

    if (isEvent) {
      f.accept(json.get("d").asObject());
    }
  }
}
