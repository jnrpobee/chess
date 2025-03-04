package dataaccess.handler;

import result.GameDataResult;

import java.util.Collection;

public record ListRequest(Collection<GameDataResult> games) {
}
