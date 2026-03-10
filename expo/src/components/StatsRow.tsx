import React from "react";
import { StyleSheet, View } from "react-native";
import { spacing } from "../theme/tokens";
import { StatTile } from "./StatTile";

type Props = {
  streak: number;
  completion30Day: number;
  totalCompletions: number;
};

export function StatsRow({ streak, completion30Day, totalCompletions }: Props) {
  return (
    <View style={styles.row}>
      <StatTile label="Current streak" value={`${streak}d`} />
      <StatTile label="30 day completion" value={`${completion30Day}%`} />
      <StatTile label="Total completions" value={`${totalCompletions}`} />
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    gap: spacing.xs,
  },
});
