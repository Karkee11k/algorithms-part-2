import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * The class BaseballElimination represents a sport division and determines which
 * teams are mathematically eliminated.
 * 
 * @author Karthikeyan
 */
public class BaseballElimination {
    private final String[] teams;               
    private final Map<String, Integer> vertices;
    private final Map<String, TeamEliminationStats> eliminationCache;
    private final int[] wins, losses, remaining;
    private final int[][] games;

    /**
     * Creates a baseball division from the file of the given filename.
     * @param filename filename of the file
     */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = in.readInt();
        teams = new String[n];
        vertices = new HashMap<>();
        eliminationCache = new HashMap<>();
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];

        for (int i = 0; i < n; i++) {
            teams[i] = in.readString();
            vertices.put(teams[i], i);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++)
                games[i][j] = in.readInt();
        }
    }

    /**
     * Returns number of teams in the baseball division.
     * @return returns the number of teams
     */
    public int numberOfTeams() {
        return teams.length;
    }

    /**
     * Returns the teams in the baseball division.
     * @return returns the teams in the baseball division
     */
    public Iterable<String> teams() {
        return vertices.keySet();
    }

    /**
     * Returns the number of wins for given team.
     * @param team the team in the baseball division
     * @throws IllegalArgumentException if team is invalid
     * @return returns number of wins for given team
     */
    public int wins(String team) {
        validateTeam(team);
        return wins[vertices.get(team)];
    }

    /**
     * Returns the number of losses for given team.
     * @param team the team in the baseball division
     * @throws IllegalArgumentException if team is invalid
     * @return returns number of losses for given team
     */
    public int losses(String team) {
        validateTeam(team);
        return losses[vertices.get(team)];
    }

    /**
     * Returns number of remaining games for given team.
     * @param team the team in the baseball division.
     * @throws IllegalArgumentException if team is invalid
     * @return returns the number of remaining games for given team
     */
    public int remaining(String team) {
        validateTeam(team);
        return remaining[vertices.get(team)];
    }

    /**
     * Returns number of remaining games between team1 and team2.
     * @param team1 the team in the baseball division
     * @param team2 the team in the baseball division
     * @throws IllegalArgumentException if team1 or team2 is invalid
     * @return returns the number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return games[vertices.get(team1)][vertices.get(team2)];
    }
    
    /**
     * Returns true if the given team is eliminated; otherwise, false.
     * @param team the team in the baseball division
     * @throws IllegalArgumentException if team is invalid
     * @return returns true if given team eliminated, else false
     */
    public boolean isEliminated(String team) {
        validateTeam(team);
        return eliminationCache.computeIfAbsent(team, this::calculateElimination).isEliminated;
    }

    /**
     * Returns a subset R of teams that eliminates given team; null if not eliminated.
     * @param team the team in the baseball division
     * @throws IllegalArgumentException if team is invalid
     * @return returns a subset R of teams that eliminates given team; null if not 
     * eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        return eliminationCache.computeIfAbsent(team, this::calculateElimination).certificate;
    }

    // calculates that the given team is eliminated mathematically or not
    private TeamEliminationStats calculateElimination(String team) {
        int n = teams.length, x = vertices.get(team);
        LinkedList<String> ls = new LinkedList<>();

        // trivial case
        for (int i = 0; i < n; i++) {
            if (wins[x] + remaining[x] < wins[i]) {
                ls.add(teams[i]);
                return new TeamEliminationStats(ls);
            }
        }

        // non trivial case
        int s = n, t = n + 1;
        FlowNetwork G = createFlowNetwork(s, t, x);
        FordFulkerson ff = new FordFulkerson(G, s, t);
        for (int i = 0; i < n; i++)
            if (ff.inCut(i)) ls.add(teams[i]);
        return new TeamEliminationStats(ls.isEmpty() ? null : ls);
    }

    // creates and returns the flow network 
    private FlowNetwork createFlowNetwork(int s, int t, int x) {
        int n = teams.length, gameVertex = t + 1;
        FlowNetwork G = new FlowNetwork(n + 2 + (n - 2) * (n - 1) / 2);
        for (int i = 0; i < n; i++) {
            if (x == i) continue;
            for (int j = i + 1; j < n; j++) {
                if (x == j) continue;
                
                // source to game vertex
                G.addEdge(new FlowEdge(s, gameVertex, games[i][j]));       
                
                // game vertex to team vertices
                G.addEdge(new FlowEdge(gameVertex, i, Double.POSITIVE_INFINITY));    
                G.addEdge(new FlowEdge(gameVertex++, j, Double.POSITIVE_INFINITY)); 
            }
            // team vertex to sink vertex
            G.addEdge(new FlowEdge(i, t, wins[x] + remaining[x] - wins[i])); 
        }
        return G;
    }

    // throw IllegalArgumentException if given team is invalid
    private void validateTeam(String team) {
        if (!vertices.containsKey(team))
            throw new IllegalArgumentException("Invalid team " + team);
    }

    // wrapper for the cache to store the results
    private class TeamEliminationStats {
        final boolean isEliminated;
        final Iterable<String> certificate;

        TeamEliminationStats(Iterable<String> certificate) {
            isEliminated = certificate != null;
            this.certificate = certificate;
        }
    }

    // test client
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");  
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}