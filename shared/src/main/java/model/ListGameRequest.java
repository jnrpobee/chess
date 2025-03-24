package model;

import result.GameDataResult;

import java.util.Collection;

public record ListGameRequest(Collection<GameDataResult> games) {
}
